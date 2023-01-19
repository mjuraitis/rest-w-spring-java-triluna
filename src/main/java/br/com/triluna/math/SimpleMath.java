package br.com.triluna.math;

import br.com.triluna.exception.UnsupportedMathOperationException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class SimpleMath {

    public Double sum(Double numberOne, Double numberTwo) {

        return numberOne + numberTwo;
    }

    @RequestMapping(value = "/subtract/{numberOne}/{numberTwo}",
            method = RequestMethod.GET)
    public Double subtract(Double numberOne, Double numberTwo) {

        return numberOne - numberTwo;
    }

    @RequestMapping(value = "/multiply/{numberOne}/{numberTwo}",
            method = RequestMethod.GET)
    public Double multiply(Double numberOne, Double numberTwo) {

        return numberOne * numberTwo;
    }

    @RequestMapping(value = "/divide/{numberOne}/{numberTwo}",
            method = RequestMethod.GET)
    public Double divide(Double numberOne, Double numberTwo) throws Exception {

        if (numberTwo == 0D) {
            throw new UnsupportedMathOperationException("Cannot divide by zero.");
        }

        return numberOne / numberTwo;
    }

    @RequestMapping(value = "/average/{numberOne}/{numberTwo}",
            method = RequestMethod.GET)
    public Double average(Double numberOne, Double numberTwo) {

        return (numberOne + numberTwo / 2D);
    }

    @RequestMapping(value = "/squareRoot/{numberOne}",
            method = RequestMethod.GET)
    public Double squareRoot(Double numberOne) {

        return Math.sqrt(numberOne);
    }
}
