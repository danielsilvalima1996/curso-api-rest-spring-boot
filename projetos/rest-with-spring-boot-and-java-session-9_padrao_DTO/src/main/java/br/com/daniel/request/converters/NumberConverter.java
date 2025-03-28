package br.com.daniel.request.converters;

import br.com.daniel.exception.ResourceNotFoundException;

public class NumberConverter {

    public static Double convertToDouble(String strNumber) {
        if (strNumber == null || strNumber.isEmpty())
            throw new ResourceNotFoundException("Please, set a numeric value");
        String number = strNumber.replaceAll(",", ".");
        return Double.parseDouble(number);
    }

    public static boolean isNumeric(String strNumber) {
        if (strNumber == null || strNumber.isEmpty()) return false;
        String number = strNumber.replaceAll(",", ".");
        return !number.matches("[-+]?[0-9]*\\.?[0-9]+");
    }

}
