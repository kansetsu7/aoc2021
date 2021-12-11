(ns kansetsu7.day12
  (:require
    [clojure.java.io :as io]))

(def example-data
  ["start-A start-b A-c A-b b-d A-end b-end"
   "dc-end HN-start start-kj dc-start dc-HN LN-dc HN-end kj-sa kj-HN kj-dc"
   "fs-end he-DX fs-he start-DX pj-DX end-zg zg-sl zg-pj pj-he RW-he fs-DX pj-RW zg-RW start-pj he-WI zg-he pj-fs start-RW"])

(defn puzzle-input
  ([] (puzzle-input (slurp (io/resource "day12.txt"))))
  ([string-data]
   (->> (re-seq #"\w+" string-data)
        (partition 2))))

(defn possible-directions
  [rough-map]
  (->> rough-map
       (map reverse)
       (into rough-map)
       (remove (fn [[start end]] (or (= "end" start) (= "start" end))))
       (map vec)))

(defn reach-end?
  [path]
  (= "end" (last path)))

(defn small-cave?
  [cave]
  (re-find #"[a-z]" cave))

(defn join-next-cave
  [path possible-dirs]
  (if (reach-end? path)
    [path]
    (->> (filter #(= (last path) (first %)) possible-dirs)
         (map #(conj path (last %))))))

(defn small-cave-visit-frequency
  [path]
  (->> (frequencies path)
       (filter (fn [[cave _]] (small-cave? cave)))))

(defn visit-more-than?
  [n visit-feq]
  (some (fn [[_ visits]] (> visits n)) visit-feq))

(defmulti invalid-access?
  (fn [_path small-cave-access-rule] small-cave-access-rule))

(defmethod invalid-access? :all-small-cave-once
  [path _rule]
  (visit-more-than? 1 (small-cave-visit-frequency path)))

(defmethod invalid-access? :once-small-cave-twice-others-once
  [path _rule]
  (let [visit-feq (small-cave-visit-frequency path)]
    (or (visit-more-than? 2 visit-feq)
        (-> (filter (fn [[_ visits]] (= visits 2)) visit-feq)
            count
            (> 1)))))

(defn join-next-possible-cave
  [exploring-paths possible-dirs small-cave-access-rule]
  (->> exploring-paths
       (mapcat #(join-next-cave % possible-dirs))
       (remove #(invalid-access? % small-cave-access-rule))))

(defn list-all-possible-paths
  [possible-dirs small-cave-access-rule]
  (let [ini-paths (filter (fn [[start _]] (= "start" start)) possible-dirs)]
    (loop [exploring-paths ini-paths]
      (if (every? reach-end? exploring-paths)
        exploring-paths
        (recur (join-next-possible-cave exploring-paths possible-dirs small-cave-access-rule))))))

(comment
  ;;part1
  (let [possible-dirs (->> (puzzle-input (nth example-data 2))
                          possible-directions)]
    (count (list-all-possible-paths possible-dirs :all-small-cave-once)))

  ;; part1: 4775
  (count (list-all-possible-paths (possible-directions (puzzle-input)) :all-small-cave-once))
  ;; part2: 152480
  (time (count (list-all-possible-paths (possible-directions (puzzle-input)) :once-small-cave-twice-others-once))))
