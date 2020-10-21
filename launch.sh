#!/bin/sh

size='size=14'
alias lemon="lemonbar -p -a 50 -f \"Ubuntu Mono:$size\" -f \"Font Awesome 5 Free:$size\" -f \"Font Awesome 5 Brands:$size\" -f \"Font Awesome 5 Free Solid:$size\""

pkill lemonbar ; pkill -f 'java.*lojbar' 

( cd path/to/lojbar/ ; java -jar target/uberjar/lojbar-0.1.0-SNAPSHOT-standalone.jar ) | lemon | bash 
