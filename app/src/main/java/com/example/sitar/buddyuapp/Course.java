package com.example.sitar.buddyuapp;

/**
 * Created by sitar on 5/14/2017.
 */

public class Course {

        //String code = null;
        String name = null;
        boolean selected = false;
        String crn=null;

        public Course(String name, boolean selected, String crn) {
            super();
            //this.code = code;
            this.name = name;
            this.selected = selected;
            this.crn=crn;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getCRN() { return crn==null?"":crn;}
        public void setCRN(String crn){this.crn=crn;}

        public boolean isSelected() {
            return selected;
        }
        public void setSelected(boolean selected) {
            this.selected = selected;
        }
        public String toString(){return name;}


}
