package test;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class testBean {

    @Max(5)
    private int number1 = 8;

    @Max(5)
    private int number2 = 8;
    @Max(5)
    private int number3 = 8;
    @Max(5)
    private int number4 = 8;
    @Max(5)
    private int number5 = 8;
    @Max(5)
    private int number6 = 8;
    @Max(5)
    private int number7 = 8;
    @Max(5)
    private int number8 = 8;
    @Max(5)
    private int number9 = 8;
    @Max(5)
    private int number10 = 8;
    @Max(5)
    private int number11 = 8;

    @NotNull
    private String name;

    @Valid
    private testBean2 bean = new testBean2();

    @Min(10)
    private int number = 9;

}
