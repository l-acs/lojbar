#!/bin/zsh

pkill lemonbar ; pkill -f lojbar

lojbar=projects/code/bar2/lojbar/lojbar
size='size=14'
alias lemon="lemonbar -p -a 50 -f \"Ubuntu Mono:$size\" -f \"Font Awesome 5 Free:$size\" -f \"Font Awesome 5 Brands:$size\" -f \"Font Awesome 5 Free Solid:$size\""

clojure $lojbar | lemon | bash &

