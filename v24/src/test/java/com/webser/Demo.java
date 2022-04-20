package com.webser;

public class Demo {
    public static void main(String[] args) {
//        Inner inner = new Inner(); wrong ,unless make Inner static
        Demo demo = new Demo();
        /**
         * 外部类实例.new Inner()
         */
        Inner inner = demo.new Inner();
    }
    public class Inner{

    }
}
