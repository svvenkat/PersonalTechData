set autochdir

"for python tab handling
set tabstop=4
set expandtab
set shiftround
set shiftwidth=4
set softtabstop=4

"Search 
set ic
set hlsearch
set incsearch
set smartcase

"set number
"set tw=79
"set nowrap
"set fo-=t
"set colorcolumn=80
"highlight ColorColumn ctermbg=233

set guifont=Monospace\ 12

"Normal Clipboard behavior
set clipboard=unnamed
set pastetoggle=<F2>
autocmd! bufwritepost .vimrc source %

set mouse=a
set bs=2

let mapleader=","

noremap <Leader>e :wq<CR> ",e to save and quit"
vnoremap <Learder>s :sort<CR>

"selecting a block and moving it using > or <
vnoremap < <gv
vnoremap > >gv

"Installing color schemes 
"mkdir ~/.vim/color && cp color scheme.vim file here.
"
set t_Co=256
color wombat256

filetype off 
filetype plugin indent on
syntax on

"Wrap the complete paragraph into less than 80 character lines.
vmap Q gp
nmap Q gqap

set history=700
set undolevels=700

"Call pathogen to find other plugin in a getter way"
"mkdir ~/.vim/autoload/. get  https://tpo.pe/pathogen.vim and copy to
"~/.vim/autoload

call pathogen#infect()

"needed for powerline plugin"
set laststatus=2

"needed for ctrlp plugin - does file opening easier"
let g:ctrlp_max_height=30
set wildignore+=*.pyc
set wildignore+=*_build/*
set wildignore+=*/coverage/*

"needed for python-mode plugin
map <Leader>g :call RopeGotoDefinition()<CR>
let ropevim_enable_shortcuts=1
let g:pymode_rope_got_def_newwin="vnew"
let g:pymode_rope_extended_complete=1
let g:pymode_breakpoint=0
let g:pymode_syntax=1
let g:pymode_syntax_builtin_objs=0
let g:pymode_syntax_builtin_funcs=0
map <Leader>b 0import ipdb; ipdb.set_trace() # BREAKPOINT <C-c>

"Better navigating through omicomplete option list
set completeopt=longest,menuone
function! OminPopup(action)
    if pubvisible()
        if a:action == 'j'
            return "\<C-N>"
        elseif a:action == 'k'
            return "\<C-P>"
        endif
    endif
    return a:action
endfunction

inoremap <silent><C-j> <C-R>=OmniPopup('j')<CR>
inoremap <silent><C-k> <C-R>=OmniPopup('k')<CR>

"python folding
"mkdir ~/.vim/ftplugin;  get python_editing.vim and copy here.
set nofoldenable


" Python mode
" Ctlr-Space for code completion
" Ctlr-P gives the list of files in the project
" <leader>b set break point
" <leader>r run the program
" Ctlr-g go the function definition
" debuggin in a debgger
" Get to know rope better
" How to work with python 2x and 3x. What is virtualenv as mentioned in pymode.txt.
" How to activate the help pymode
" Why ipdb is chosen (not pdb or others when ipdb import gives error)
" Cut & Paste doesn't work
