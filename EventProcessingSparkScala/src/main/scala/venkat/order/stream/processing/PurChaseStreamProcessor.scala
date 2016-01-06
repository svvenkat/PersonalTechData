package venkat.order.stream.processing

import org.apache.spark.SparkConf
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext

import com.google.gson.Gson

trait myMsg  {
  def isValid : Boolean
}
//TODO : Make the fields as Option so that the use of null can be avoided.
case class Customer(name: String, Id: Int, age:Int, address : String) extends myMsg {
  override def toString = "Name " + name + " Id " + Id + " Address" + address
  override def isValid = name != null && Id != 0 && address != null
}
case class Product(name: String, desc:String, Id:Int, price :Int) extends myMsg {
  override def toString = "Name " + name + " Id " + Id + " desc " + desc + " price "  + price
  override def isValid = name != null && Id != 0 && desc != null && price != 0
}
case class Purchase(purchaser: Int, productId: Int) extends myMsg {
  override def toString = "purchaser Id " + purchaser + " productId " + productId
  override def isValid =  purchaser != 0 && productId != 0
}

object PurChaseStreamProcessor {
  val gson = new Gson()
  def main(args : Array[String]): Unit = {
    val checkpointDir = "file:///C:\\Users\\venkat\\checkpoint\\"
    val ssc = new StreamingContext(new SparkConf().setAppName(" Purchase Stream Application"), Seconds(30))
    val dstream = ssc.textFileStream("file:///C:\\Users\\venkat\\tmp\\SparkStreaming\\")
    ssc.checkpoint(checkpointDir)
    // Read Customer records from Stream. Drop Product/Purchase records.
    val customerStream = dstream.map(x=> {
      try {
        val myObj : Customer = gson.fromJson(x, classOf[Customer])
        (myObj.Id, myObj)
      } catch {
        case e: Exception =>
          (0, new Customer(null, 0,0, null))
      }
    }).filter(_._2.isValid)
    // Read Product record from the Stream. Drop Customer/Purchase Record.
    val productStream = dstream.map(x=> {
      try {
        val myObj : Product = gson.fromJson(x, classOf[Product])
        (myObj.Id, myObj)
      } catch {
        case e: Exception =>
          (0, new Product(null, null, 0,0) )
      }
    }).filter(_._2.isValid)

    def updateFunction[T]( x : Seq[T], y : Option[T]) : Option[T] = {
      if (x.nonEmpty) {
        Some(x.head)
      }
      else if (y.isDefined) {
        y
      }
      else {
        None
      }
    }
    //Updates to the Customer/Product record will override previous values.
    //The ProductStream is persisted as it is joined with Purchase record regularly.
    val finalProductStream= productStream.updateStateByKey(updateFunction[Product])
    val finalCustomerStream=customerStream.updateStateByKey(updateFunction[Customer])

    finalCustomerStream.foreachRDD(x=>x.collect().foreach(println))
    finalProductStream.persist()
    finalProductStream.foreachRDD(x => x.collect().foreach(println))

    val purchaseStream = dstream.map(x=> {
      try {
        val myObj : Purchase = gson.fromJson(x, classOf[Purchase])
        (myObj.productId, myObj.purchaser)
      } catch {
        case e: Exception =>
          (-1, -1)
      }
    }).filter( _._2 > 0)
    //TODO : Join with the customerStream and productStream separately to eliminate the invalid customer/product records.

    val joinedStream = purchaseStream.join(finalProductStream).map( x => {
      x match {
        case (productId, (purchaserId, Product(name, desc, _, price))) => (productId, (purchaserId, price))
      }
    })
    joinedStream.persist()
    val ProductSaleAggregation = joinedStream.reduceByKey( (x,y) => ( x._1, x._2 + y._2)).
      map( x => x match {
      case (productId, (purchaserId, sumprice)) => (productId, sumprice)
    })
    val PurchaserSaleAggregation = joinedStream.map( x => x match {
      case (productId, (purchaserId, price)) => (purchaserId, price)
    }).reduceByKey((x,y) => x + y)

    ProductSaleAggregation.foreachRDD(x=> x.collect().foreach(println))
    PurchaserSaleAggregation.foreachRDD(x=> x.collect().foreach(println))

    ssc.start()
    ssc.awaitTermination()
  }
}