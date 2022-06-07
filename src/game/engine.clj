(ns game.engine
  (:gen-class)
  (:require [clojure.string :as str]
            [clojure.tools.trace :as trace]))

;; loss if states from all direction moves give the same state



(defn find-empty-locations [{:keys [board empty-value size]}]
  (for [row (range size)
        col (range size)
        :when (= empty-value (get-in board [row col]))]
    [row col]))

(defn add-value [{:keys [board new-values] :as game}]
  (let [empty-locations (find-empty-locations game)
        location-to-fill (rand-nth empty-locations)
        new-value (rand-nth new-values)
        new-board (if (empty? empty-locations)
                    board
                    (assoc-in board location-to-fill new-value))]
    (assoc game :board new-board)))

(defn get-start-game [{:keys [size empty-value winning-value] :as game}]
  (let [empty-board (vec (repeat size (vec (repeat size empty-value))))
        value-width (count (str winning-value))
        updated-game (assoc game
                            :value-width value-width
                            :board empty-board)]
    (add-value updated-game)))

(defn merge-values [[fst snd & rst :as values]]
  (cond
    (<= (count values) 1) values
    (= fst snd) (concat [(* 2 fst)] (merge-values rst))
    :else (concat [fst] (merge-values (rest values)))))

; merging on the left movement
(defn move-left [row {:keys [size empty-value]}]
  (let [non-empty-values (remove #(= empty-value %) row)
        merged-values (merge-values non-empty-values)]
    (vec (take size (concat merged-values (repeat empty-value))))))

(defn move-left-board [{:keys [board] :as game}]
  (assoc game :board (mapv #(move-left % game) board)))

(defn rotate-right-value [size old-board new-board [row col]]
  (assoc-in new-board [col (- (dec size) row)] (get-in old-board [row col])))

(defn rotate-right [{:keys [board size] :as game}]
  (let [positions (for [row (range size)
                        col (range size)]
                    [row col])
        rotated-board (reduce (partial rotate-right-value size board) board positions)]
    (assoc game :board rotated-board)))

(def rotate-180 (comp rotate-right rotate-right))

(def rotate-left (comp rotate-right rotate-right rotate-right))

(defn move [game direction]
  (case direction
    :up  (-> game
             rotate-left
             move-left-board
             rotate-right)
    :down (-> game
              rotate-right
              move-left-board
              rotate-left)
    :left (move-left-board game)
    :right (-> game
               rotate-180
               move-left-board
               rotate-180)
    game))

(defn get-status [{:keys [board winning-value] :as game}]
  (let [max-num (->> board flatten (apply max))]
    (cond
      (>= max-num winning-value) :won
      (apply = (conj (map #(move game %) [:up :down :left :right])
                     game)) :lost
      :else :none)))

(defn play-game [game]
  (->> game :board (str/join "\n") println)
  (let [status (get-status game)]
    (case status
      :won (println "You won!")
      :lost (println "You lost.")
      (let [dir (read-line)
            new-game (move game (get {"u" :up
                                      "d" :down
                                      "r" :right
                                      "l" :left} dir :none))
            new-game-2 (if (= (:board game) (:board new-game))
                         new-game
                         (add-value new-game))] (play-game new-game-2)))))

(defn -main
  []
  (println "Controls\n--------\n u: up\n d: down\n r: right\n l: left\n")
  (play-game (get-start-game {:size 4
                              :empty-value 0
                              :winning-value 2048
                              :new-values [2 2 2 2 4]})))
