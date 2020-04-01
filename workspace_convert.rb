#takes any number of workspaces as input, prefixed with * for shown and - for hidden

#convert this into pretty lemonbar output

def getwm()
  `#{"wmctrl -m  | head -n 1 | cut -f2 -d ' ' "}`
end

def makebutton(message, actions)
  #button is index + 1
  #1 to 7: left, middle, right, scroll up, scroll down, hover enter, hover leave
  "%{A1:" + actions[0] + ":} " + message +
    " %{A}" #one for each
end



def getdesktoplist(wm)
  case
  when wm.match("bspwm"), wm.match("cwm"), wm.match("CWM")
    %x<for i in $(wmctrl -d | tr -s '[:blank:]' | awk '{print $2$10}' ); do echo -n "$i,"; done>

  
#    %x<for i in $(wmctrl -d | tr -s '[:blank:]' | awk '{print $2$10}' ); do echo -n "$i,"; done>

  else
    puts "something else"

  end .split(",")
end

def bspwm_lemonbar_desktops
  (getdesktoplist "bspwm").each_with_index.map{
    |name, index|  makebutton(name, ["wmctrl -s " + (index).to_s])
  } * " "
end


#todo refactor buttonmaking


#music
def mpd_out
  %x< mpc | head -n 1>.chomp +
    makebutton("prev", ["mpc prev"]) +
    makebutton("toggle", ["mpc toggle"]) +
    makebutton("next", ["mpc next"])
  


end

def date
  %x<date "+%A, %B %-d, %Y - %I:%M%P">.chomp
end










fore = "%{F#FFFFFF}"
back = "%{B#222222}" 
puts fore + back + "%{l}" + bspwm_lemonbar_desktops + "%{c}" + mpd_out + "%{r}" + date + "    "

