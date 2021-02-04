(ns lojbar.handler
  (:gen-class)
  (:use
   [clojure.java.shell :only [sh]])
  (:require
   [clojure.string :as str]))

(defn exec [& args]
  (->> args list flatten (str/join " ") (sh "sh" "-c") :out str/split-lines first))

(defn convert-interval-to-ms [i]
  (if-let [m (re-matches #".*[hms]" (str i))]
    (let [suffix (last m)
          num (->> m (drop-last 1) str/join read-string)]
      (case suffix
        \s (* num 1000)
        \m (* num 1000 60)
        \h (* num 1000 60 60)))
    i))

(defn determine-content-type
  [s]
  (if (= (type s) java.lang.String)
    (case (str (first s))
      "`" :command
      "/" :file
      :text)
    :clojure))

(defn strip-cmd [cmd]
  (str/replace cmd #"(`$|^`)" ""))

(defn output-handler [output]
  (case (determine-content-type output)
    :text output
    :clojure (-> output eval)
    :command (-> output strip-cmd exec)
    :file (slurp output)))
