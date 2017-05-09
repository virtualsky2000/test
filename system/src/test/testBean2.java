package test;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import system.validator.Order;

public class testBean2 {

    @NotNull
    private String abc = null;

    @Max(80)
    @Order(order = 2)
    private int year = 90;

    @NotNull
    private String zzz = null;

    @NotNull
    @Order(order = 1)
    private String id;

    @NotNull
    private String xxx = null;

}
