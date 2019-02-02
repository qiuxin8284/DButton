package com.sfr.dbuttonapplication.gson;

import java.util.List;

/**
 * Created by ASUS on 2018/8/24.
 */

public class JinshanEnglishToChinesePartBean {
    /**
     * word_name : good
     * is_CRI : 1
     * exchange : {"word_pl":["goods"],"word_third":"","word_past":"","word_done":"","word_ing":"","word_er":["better"],"word_est":["best"]}
     * symbols : [{"ph_en":"g?d","ph_am":"ɡ?d","ph_other":"","ph_en_mp3":"http://res.iciba.com/resource/amp3/oxford/0/28/a2/28a24294fed307cf7e65361b8da4f6e5.mp3","ph_am_mp3":"http://res.iciba.com/resource/amp3/1/0/75/5f/755f85c2723bb39381c7379a604160d8.mp3","ph_tts_mp3":"http://res-tts.iciba.com/7/5/5/755f85c2723bb39381c7379a604160d8.mp3","parts":[{"part":"adj.","means":["好的","优秀的","有益的","漂亮的，健全的"]},{"part":"n.","means":["好处，利益","善良","善行","好人"]},{"part":"adv.","means":["同well"]}]}]
     */

    private String word_name;
    private String is_CRI;
    private ExchangeBean exchange;
    private List<SymbolsBean> symbols;

    public String getWord_name() {
        return word_name;
    }

    public void setWord_name(String word_name) {
        this.word_name = word_name;
    }

    public String getIs_CRI() {
        return is_CRI;
    }

    public void setIs_CRI(String is_CRI) {
        this.is_CRI = is_CRI;
    }

    public ExchangeBean getExchange() {
        return exchange;
    }

    public void setExchange(ExchangeBean exchange) {
        this.exchange = exchange;
    }

    public List<SymbolsBean> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<SymbolsBean> symbols) {
        this.symbols = symbols;
    }

    public static class ExchangeBean {
        /**
         * word_pl : ["goods"]
         * word_third :
         * word_past :
         * word_done :
         * word_ing :
         * word_er : ["better"]
         * word_est : ["best"]
         */

        private String word_third;
        private String word_past;
        private String word_done;
        private String word_ing;
        private List<String> word_pl;
        private List<String> word_er;
        private List<String> word_est;

        public String getWord_third() {
            return word_third;
        }

        public void setWord_third(String word_third) {
            this.word_third = word_third;
        }

        public String getWord_past() {
            return word_past;
        }

        public void setWord_past(String word_past) {
            this.word_past = word_past;
        }

        public String getWord_done() {
            return word_done;
        }

        public void setWord_done(String word_done) {
            this.word_done = word_done;
        }

        public String getWord_ing() {
            return word_ing;
        }

        public void setWord_ing(String word_ing) {
            this.word_ing = word_ing;
        }

        public List<String> getWord_pl() {
            return word_pl;
        }

        public void setWord_pl(List<String> word_pl) {
            this.word_pl = word_pl;
        }

        public List<String> getWord_er() {
            return word_er;
        }

        public void setWord_er(List<String> word_er) {
            this.word_er = word_er;
        }

        public List<String> getWord_est() {
            return word_est;
        }

        public void setWord_est(List<String> word_est) {
            this.word_est = word_est;
        }
    }

    public static class SymbolsBean {
        /**
         * ph_en : g?d
         * ph_am : ɡ?d
         * ph_other :
         * ph_en_mp3 : http://res.iciba.com/resource/amp3/oxford/0/28/a2/28a24294fed307cf7e65361b8da4f6e5.mp3
         * ph_am_mp3 : http://res.iciba.com/resource/amp3/1/0/75/5f/755f85c2723bb39381c7379a604160d8.mp3
         * ph_tts_mp3 : http://res-tts.iciba.com/7/5/5/755f85c2723bb39381c7379a604160d8.mp3
         * parts : [{"part":"adj.","means":["好的","优秀的","有益的","漂亮的，健全的"]},{"part":"n.","means":["好处，利益","善良","善行","好人"]},{"part":"adv.","means":["同well"]}]
         */

        private String ph_en;
        private String ph_am;
        private String ph_other;
        private String ph_en_mp3;
        private String ph_am_mp3;
        private String ph_tts_mp3;
        private List<PartsBean> parts;

        public String getPh_en() {
            return ph_en;
        }

        public void setPh_en(String ph_en) {
            this.ph_en = ph_en;
        }

        public String getPh_am() {
            return ph_am;
        }

        public void setPh_am(String ph_am) {
            this.ph_am = ph_am;
        }

        public String getPh_other() {
            return ph_other;
        }

        public void setPh_other(String ph_other) {
            this.ph_other = ph_other;
        }

        public String getPh_en_mp3() {
            return ph_en_mp3;
        }

        public void setPh_en_mp3(String ph_en_mp3) {
            this.ph_en_mp3 = ph_en_mp3;
        }

        public String getPh_am_mp3() {
            return ph_am_mp3;
        }

        public void setPh_am_mp3(String ph_am_mp3) {
            this.ph_am_mp3 = ph_am_mp3;
        }

        public String getPh_tts_mp3() {
            return ph_tts_mp3;
        }

        public void setPh_tts_mp3(String ph_tts_mp3) {
            this.ph_tts_mp3 = ph_tts_mp3;
        }

        public List<PartsBean> getParts() {
            return parts;
        }

        public void setParts(List<PartsBean> parts) {
            this.parts = parts;
        }

        public static class PartsBean {
            /**
             * part : adj.
             * means : ["好的","优秀的","有益的","漂亮的，健全的"]
             */

            private String part;
            private List<String> means;

            public String getPart() {
                return part;
            }

            public void setPart(String part) {
                this.part = part;
            }

            public List<String> getMeans() {
                return means;
            }

            public void setMeans(List<String> means) {
                this.means = means;
            }
        }
    }
}
