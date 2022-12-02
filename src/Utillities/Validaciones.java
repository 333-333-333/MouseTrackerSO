package Utillities;

import java.util.Scanner;

public class Validaciones {

    /**
     * Dada una cota inferior o una cota superior, se retorna un número, y se
     * asegura que su valor esté en el rango comprendido entre estas.
     * @param mínimo
     * @param máximo
     * @return
     */
    public static int validarIntervalo(int mínimo, int máximo) {
        Scanner input = new Scanner(System.in);
        try {
            if (mínimo >= máximo) {
                throw new Exception("El mínimo no puede ser mayor al máximo");
            }

            int número = input.nextInt();

            if (número >= mínimo && número <= máximo) {
                return número;
            } else {
                throw new Exception("El número está fuera del intervalo");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return validarIntervalo(mínimo, máximo);
        }
    }

    /**
     * Se retorna un número estrictamente positivo y menor al entero máximo
     * procesable en un sistema numérico de 32 bits.
     * @return
     */
    public static int validarPositivo() {
        Scanner input = new Scanner(System.in);
        try {
            int número = input.nextInt();

            if (número <= 0 || número >= Integer.MAX_VALUE) {
                throw new Exception("El valor numérico no es válido");
            }

            return  número;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return validarPositivo();
        }
    }

}
