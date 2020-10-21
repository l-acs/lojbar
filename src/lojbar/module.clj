(ns lojbar.module
  (:gen-class)
  (:require
   [clojure.string :as str])
  (:use
   [lojbar.subscribe :only
    [sync-atom-content-with-cmd-updates! sync-atom-content-with-file-changes!]])
  (:refer lojbar.handler))

;; a module consists of 1) an 'active' atom containing text to
;; display, 2) fg/bg colors, and 3) a hash map of buttons

(defn make-subscription-atom
  "Create an atom in which to store the result of (f) whenever resource changes"
  [resource f]
  (let [out-atom (atom nil)]
    (case (determine-content-type resource)
      :file (sync-atom-content-with-file-changes! out-atom f resource)
      :command (apply sync-atom-content-with-cmd-updates! out-atom f (-> resource strip-cmd (str/split #"\s"))))
    out-atom))

(defn make-polling-atom
  "Generate an atom containing the result of (f) which updates at intervals of freq"
  [freq f]
  (let [out-atom (atom nil)]
    (future
      (do
        (while true
          (reset! out-atom (f))
          (Thread/sleep freq))))
    out-atom))

(defn make-module
  "Given a user-defined module as a hashmap, return a new hashmap also
  containing an updating atom with text to display."
  [cfgmodule]
  (let [outf #(output-handler (:output cfgmodule))
        interval (:update-interval cfgmodule)
        resource (:watch cfgmodule)]    
    (assoc cfgmodule
           :atom
           ;; if there's something to subscribe to, subscribe to it
           (if resource 
             (make-subscription-atom resource outf)
             ;; if the function should be updated at an interval, do so
             (if interval 
               (make-polling-atom (convert-interval-to-ms interval) outf)
               ;; otherwise, just evaluate it once and put it in an atom
               (atom (outf)))))))
