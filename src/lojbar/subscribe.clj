(ns lojbar.subscribe
  (:gen-class)
  (:require
   [clojure.string :as str])
  (:import
   java.lang.Runtime
   java.io.BufferedReader
   java.io.InputStreamReader))

(defn store-cmd-in-atom! 
  "Whenever a command emits a new line, `reset!` a to that line"
  [a & cmdargs]
  (future
    (let [proc (.exec (Runtime/getRuntime) (into-array cmdargs))]
      (with-open [rdr (BufferedReader. (InputStreamReader. (.getInputStream proc)))]
        (dorun
         (map #(reset! a %)
              (take-while  #(pos? (.length %))
                           (repeatedly #(.readLine rdr)))))))))

(defn sync-atom-content-with-cmd-updates!
  "Whenever a command emits a new line, `reset!` a to the result of (f)"
  [a f & cmdargs]
  (future
    (let [proc (.exec (Runtime/getRuntime) (into-array cmdargs))]
      (with-open [rdr (BufferedReader. (InputStreamReader. (.getInputStream proc)))]
        (dorun
         (map #(do
                 (comment %) ;; this seems to be necessary to force 
                 (reset! a (f))) ;; this to be evaluated given laziness
              (take-while  #(pos? (.length %))
                           (repeatedly #(.readLine rdr)))))))))

(defn sync-atom-content-with-file-changes!
  "Whenever a file changes, `reset!` a to the result of (f)"
  [a f path]
  (sync-atom-content-with-cmd-updates! a f "inotifywait" "-m" "-e" "modify" path))

(defn run-on-atom-change [a f]
  (add-watch a :watcher
             (fn [key atom old-state new-state] (f))))
