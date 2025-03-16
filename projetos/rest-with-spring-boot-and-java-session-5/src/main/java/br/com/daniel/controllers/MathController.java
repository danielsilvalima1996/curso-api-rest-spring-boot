package br.com.daniel.controllers;

import br.com.daniel.exception.UnsupportedMathOperationException;
import br.com.daniel.math.SimpleMath;
import br.com.daniel.request.converters.NumberConverter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/math")
public class MathController {

    private final SimpleMath math = new SimpleMath();

    @RequestMapping("/sum/{numberOne}/{numberTwo}")
    public Double sum(
            @PathVariable("numberOne") String numberOne,
            @PathVariable("numberTwo") String numberTwo) {
        if (NumberConverter.isNumeric(numberOne) || NumberConverter.isNumeric(numberTwo))
            throw new UnsupportedMathOperationException("Please, set a numeric value");
        return math.sum(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping("/subtraction/{numberOne}/{numberTwo}")
    public Double subtraction(
            @PathVariable("numberOne") String numberOne,
            @PathVariable("numberTwo") String numberTwo) {
        if (NumberConverter.isNumeric(numberOne) || NumberConverter.isNumeric(numberTwo))
            throw new UnsupportedMathOperationException("Please, set a numeric value");
        return math.subtraction(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping("/multiplication/{numberOne}/{numberTwo}")
    public Double multiplication(
            @PathVariable("numberOne") String numberOne,
            @PathVariable("numberTwo") String numberTwo) {
        if (NumberConverter.isNumeric(numberOne) || NumberConverter.isNumeric(numberTwo))
            throw new UnsupportedMathOperationException("Please, set a numeric value");
        return math.multiplication(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping("/division/{numberOne}/{numberTwo}")
    public Double division(
            @PathVariable("numberOne") String numberOne,
            @PathVariable("numberTwo") String numberTwo) {
        if (NumberConverter.isNumeric(numberOne) || NumberConverter.isNumeric(numberTwo))
            throw new UnsupportedMathOperationException("Please, set a numeric value");
        return math.division(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping("/mean/{numberOne}/{numberTwo}")
    public Double mean(
            @PathVariable("numberOne") String numberOne,
            @PathVariable("numberTwo") String numberTwo) {
        if (NumberConverter.isNumeric(numberOne) || NumberConverter.isNumeric(numberTwo))
            throw new UnsupportedMathOperationException("Please, set a numeric value");
        return math.mean(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping("/squareRoot/{numberOne}")
    public Double squareRoot(
            @PathVariable("numberOne") String numberOne) {
        if (NumberConverter.isNumeric(numberOne))
            throw new UnsupportedMathOperationException("Please, set a numeric value");
        return math.squareRoot(NumberConverter.convertToDouble(numberOne));
    }

}
