(ns kansetsu7.day1
  (:require
    [clojure.java.io :as io]
    [clojure.string :as cs]))

(defn puzzle-input
  []
  (as-> (slurp (io/resource "day1.txt")) $
        (cs/split $ #"\n")
        (map #(Integer/parseInt %) $)))

(defn incerase?
  [previous curent]
  (> previous curent))

(defn get-increase
  [coll]
  (->> (map incerase?
            (rest coll)
            (pop (vec coll)))
       (filter #(true? %))
       count))

(comment
  ;; part1: 1692
  (-> (puzzle-input)
      get-increase)

  ;; part2: 1724
  (->> (partition 3 1 (puzzle-input))
       (map #(apply + %))
       get-increase))
