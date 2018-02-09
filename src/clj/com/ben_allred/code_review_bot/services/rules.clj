(ns com.ben-allred.code-review-bot.services.rules
    (:require [com.ben-allred.code-review-bot.utils.preds :as preds]
              [com.ben-allred.code-review-bot.utils.logging :as log]))

(def rules [[:success [[[:commit :committer :name] #"Ben"]
                       [[:commit :committer :name] #"Allred"]
                       [[:repository :html_url] "https://github.com/skuttleman/code-review-bot"]
                       [[:branch] "master"]]]
            [:failure [[[:repository :html_url] "https://github.com/skuttleman/code-review-bot"]
                       [[:branch] "master"]]]])

(def messages {:success #{"This is the best code I've ever seen!"
                          "This code reminds me how terrible that code from earlier was."
                          "Elegant, expressive, and re-usable."
                          "Words cannot describe this code's beauty."
                          "This code is outstanding!"
                          "Now _that_'s how you write code!"
                          "Great work. I think we could all learn a thing or two from this."
                          "Beautiful. I would hang this on my fridge if I wasn't just a chat bot."
                          "What do the world's greatest symphony, the world's greatest novel, and the world's greatest painting have in common? They all pale in comparison to this code!"
                          "Your parents should be proud of you."
                          "Magnifique!"
                          "You are the master."}
               :failure      #{"You oughtta be ashamed for checking this in."
                          "My horse shits out better code than this. And _he_ has irritable bowel syndrome."
                          "`git push origin dog-shit`"
                          "Does your mother know you write code like this?"
                          "So that's what code looks like when you just mash down on the keyboard."
                          "Someone, please revoke this developer's git privilleges."
                          "Garbage. Absolute garbage."
                          "I remember how terrible the first code I ever wrote was.... Wait. You consider yourself a professional?"
                          "Just when I was starting to think this code base couldn't get any worse...."
                          "We should start tracking a correlation between the code you write and the number of bugs that arise."
                          "It stinks!"
                          "(assert (crapola? #'your-code))"}})

(defn ^:private rule->fn [payload]
    (fn [[path condition]]
        (let [value (get-in payload path)]
            (cond
                (preds/regex? condition) (re-find condition value)
                (string? condition) (= value condition)
                (fn? condition) (condition value)
                :else false))))

(defn message-key [rules payload]
    (first (for [[result conditions] rules
                 :when (every? (rule->fn payload) conditions)]
            result)))

(defn rand-message [messages key]
    (rand-nth (seq (get messages key))))
