(ns lojbar.custom
  (:gen-class)
  (:require
   [clojure.string :as str]
   [lojbar.format :as format]))

(def default-color format/default-color)
(defn bat-emoji [offset partitions]
  (let [bat (-> "/sys/class/power_supply/BAT0/capacity"
                slurp
                clojure.string/trim-newline
                read-string)]
    (str
     (->> bat
          (+ 24 (- offset))
          (* (/ partitions 100))
          int
          (- 4)
          (+ 62016)
          Character/toChars
          (apply str))
     ":" bat "%")))

(defn get-desktop-list [output]
  (filter #(re-matches #"[fFoO].*" %) (str/split output #":")))

(defn is-active? [desktop]
  (re-matches #"[A-Z].*" desktop))

(defn has-windows? [desktop]
  (re-matches #"[oO].*" desktop))

(defn get-name [desktop]
  (-> desktop (str/split #"")  rest str/join))

(defn desktop-format-str [desktop padding normal-color focus-color]
  (let [d (str (:left padding) (get-name desktop) (:right padding))]
    (if (is-active? desktop)
      (str focus-color d default-color)
      (when (has-windows? desktop)
        (str normal-color d default-color)))))

(defn desktop-button [desktop padding normal-color focus-color index]
  (when-let [d (desktop-format-str desktop padding normal-color focus-color)]
    (format/make-buttons-lemonbar
     d
     {:left (str "bspc config pointer_follows_focus false; $HOME/.scripts/window/act.sh --capturefocuscapture " index " ; bspc config pointer_follows_focus true ")
      :right (str "$HOME/.scripts/window/act.sh -s " index)
      :in (str "$HOME/.scripts/window/act.sh --showthumbnail " index)})))


(defn show-desktops 
  [output padding normal-color focus-color]
  (->> output
       get-desktop-list
       (keep-indexed #(desktop-button %2 padding normal-color focus-color (+ %1 1)))
       (str/join (:joiner padding))))
