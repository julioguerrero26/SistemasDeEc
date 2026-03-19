import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    static final String RESET = "\033[0m";
    static final String BOLD = "\033[1m";
    static final String RED = "\033[31m";
    static final String GREEN = "\033[32m";
    static final String YELLOW = "\033[33m";
    static final String CYAN = "\033[36m";
    static final String CLEAR = "\033[H\033[2J";

    // Variables globales para recordar la ecuación en memoria (Opción 1)
    static int gradoMemoria = -1;
    static double[] coeficientesMemoria = null;
    static double esMemoria = 0;

    static class Ecuacion {
        int a, b, c;
        char v1, v2;
        int p1, p2;
        boolean valida;
    }

    // ... (El analizador de ecuaciones se mantiene intacto)
    static Ecuacion analizarEcuacion(String strEc) {
        Ecuacion ec = new Ecuacion();
        ec.valida = false;
        try {
            String[] partes = strEc.split("=");
            if (partes.length != 2) return ec;

            String izq = partes[0].replaceAll("\\s+", "");
            String der = partes[1].replaceAll("\\s+", "");

            int c_total = Integer.parseInt(der);

            Pattern pBinomio = Pattern.compile("([+-]?\\d*)\\(([a-zA-Z])([+-]\\d+)\\)");
            Matcher mBinomio = pBinomio.matcher(izq);
            StringBuffer sb = new StringBuffer();

            while (mBinomio.find()) {
                String strCoefExt = mBinomio.group(1);
                String var = mBinomio.group(2);
                String strConstInt = mBinomio.group(3);

                int coefExt = 1;
                if (strCoefExt.equals("-")) coefExt = -1;
                else if (strCoefExt.equals("+") || strCoefExt.isEmpty()) coefExt = 1;
                else coefExt = Integer.parseInt(strCoefExt.replace("+", ""));

                int constInt = Integer.parseInt(strConstInt.replace("+", ""));

                int nuevoCoefVar = coefExt;
                int nuevaConstante = coefExt * constInt;

                String reemplazo = (nuevoCoefVar >= 0 ? "+" : "") + nuevoCoefVar + var +
                        (nuevaConstante >= 0 ? "+" : "") + nuevaConstante;
                mBinomio.appendReplacement(sb, reemplazo);
            }
            mBinomio.appendTail(sb);
            izq = sb.toString();

            Pattern pTermino = Pattern.compile("([+-]?\\d*)([a-zA-Z])(?:\\^(\\d+))?");
            Matcher mRemocion = pTermino.matcher(izq);
            String soloConstantes = mRemocion.replaceAll("");

            int constantesIzquierda = 0;
            Pattern pConst = Pattern.compile("[+-]?\\d+");
            Matcher mConst = pConst.matcher(soloConstantes);
            while (mConst.find()) {
                constantesIzquierda += Integer.parseInt(mConst.group().replace("+", ""));
            }

            ec.c = c_total - constantesIzquierda;

            Matcher mTermino = pTermino.matcher(izq);
            int contador = 0;

            while (mTermino.find()) {
                String strCoef = mTermino.group(1);
                char var = mTermino.group(2).charAt(0);
                String strPot = mTermino.group(3);

                int coef = 1;
                if (strCoef.equals("-")) coef = -1;
                else if (!strCoef.isEmpty() && !strCoef.equals("+")) {
                    coef = Integer.parseInt(strCoef.replace("+", ""));
                }

                int pot = 1;
                if (strPot != null && !strPot.isEmpty()) pot = Integer.parseInt(strPot);

                if (contador == 0) {
                    ec.a = coef; ec.v1 = var; ec.p1 = pot;
                } else if (contador == 1) {
                    ec.b = coef; ec.v2 = var; ec.p2 = pot;
                }
                contador++;
            }

            if (contador == 2) ec.valida = true;

        } catch (Exception ignore) {
        }
        return ec;
    }

    static void sistemaDeEcuacion() {
        // ... (El código de sistemas 2x2 se mantiene intacto)
        boolean datosConfirmados = false;
        while (!datosConfirmados) {
            System.out.print(CLEAR);
            System.out.flush();
            System.out.println(BOLD + CYAN + "<h> Calculadora de Sistemas 2x2 </h>" + RESET);
            Ecuacion ec1 = null;
            Ecuacion ec2 = null;

            while (true) {
                String str1 = IO.leerLinea(YELLOW + "Ingresa la primera ecuación (ej. 2(x+3) - 3y = 15): " + RESET);
                ec1 = analizarEcuacion(str1);
                if (ec1.valida) break;
                System.out.println(RED + "¡Error! Ingresa una ecuación válida." + RESET);
            }

            while (true) {
                String str2 = IO.leerLinea(YELLOW + "Ingresa la segunda ecuación (ej. 3(x-2) + 2(y+4) = 16): " + RESET);
                ec2 = analizarEcuacion(str2);
                if (ec2.valida) {
                    if ((ec1.v1 == ec2.v1 && ec1.p1 == ec2.p1 && ec1.v2 == ec2.v2 && ec1.p2 == ec2.p2) ||
                            (ec1.v1 == ec2.v2 && ec1.p1 == ec2.p2 && ec1.v2 == ec2.v1 && ec1.p2 == ec2.p1)) {

                        if (ec1.v1 != ec2.v1 || ec1.p1 != ec2.p1) {
                            int tempA = ec2.a; ec2.a = ec2.b; ec2.b = tempA;
                            char tempV = ec2.v1; ec2.v1 = ec2.v2; ec2.v2 = tempV;
                            int tempP = ec2.p1; ec2.p1 = ec2.p2; ec2.p2 = tempP;
                        }
                        break;
                    } else {
                        System.out.println(RED + "¡Error! Las variables y potencias deben ser consistentes." + RESET);
                    }
                } else {
                    System.out.println(RED + "¡Error! Ingresa una ecuación válida." + RESET);
                }
            }

            int a1 = ec1.a, b1 = ec1.b, c1 = ec1.c;
            int a2 = ec2.a, b2 = ec2.b, c2 = ec2.c;
            char v1 = ec1.v1, v2 = ec1.v2;
            String p1Str = ec1.p1 > 1 ? "^" + ec1.p1 : "";
            String p2Str = ec1.p2 > 1 ? "^" + ec1.p2 : "";
            String signoB1 = (b1 < 0) ? "-" : "+";
            String signoB2 = (b2 < 0) ? "-" : "+";

            System.out.printf("\n%s%sTu sistema de ecuaciones (simplificado) es:%s\n(1) %d%c%s %s %d%c%s = %d\n(2) %d%c%s %s %d%c%s = %d\n",
                    BOLD, CYAN, RESET, a1, v1, p1Str, signoB1, Math.abs(b1), v2, p2Str, c1, a2, v1, p1Str, signoB2, Math.abs(b2), v2, p2Str, c2);

            String confirmacion = IO.leerLinea("\n" + YELLOW + "¿Está correcto? (s/n): " + RESET);
            if (confirmacion.equalsIgnoreCase("s")) {
                datosConfirmados = true;
                String metodo = IO.leerLinea("\n" + YELLOW + "¿Por qué método resolver? ([S]Sustitución/[I]Igualación): " + RESET);
                if (metodo.equalsIgnoreCase("S")) resolverPorSustitucion(a1, b1, c1, a2, b2, c2, v1, p1Str, v2, p2Str);
                else if (metodo.equalsIgnoreCase("I")) resolverPorIgualacion(a1, b1, c1, a2, b2, c2, v1, p1Str, v2, p2Str);
                else System.out.println(RED + "Método no reconocido." + RESET);
            } else {
                System.out.println("\n" + RED + "Reintentando..." + RESET);
                try { Thread.sleep(1500); } catch (InterruptedException ignore) {}
            }
        }
    }

    static void resolverPorSustitucion(int a1, int b1, int c1, int a2, int b2, int c2, char v1, String p1Str, char v2, String p2Str) {
        double determinanteDet = a1 * b2 - a2 * b1;
        if (determinanteDet != 0) {
            double x = (double) (c1 * b2 - c2 * b1) / determinanteDet;
            double y = (double) (a1 * c2 - a2 * c1) / determinanteDet;
            System.out.printf("\n%s[Sustitución] Solución:%s\n %c%s = %.3f\n %c%s = %.3f\n", GREEN, RESET, v1, p1Str, x, v2, p2Str, y);
        } else {
            System.out.println("\n" + RED + "El sistema no tiene solución única." + RESET);
        }
    }

    static void resolverPorIgualacion(int a1, int b1, int c1, int a2, int b2, int c2, char v1, String p1Str, char v2, String p2Str) {
        double det = (a1 * b2) - (a2 * b1);
        if (det != 0) {
            double y = (double) (a1 * c2 - a2 * c1) / det;
            double x = (a1 != 0) ? (c1 - b1 * y) / a1 : (c2 - b2 * y) / a2;
            System.out.printf("\n%s[Igualación] Solución:%s\n %c%s = %.3f\n %c%s = %.3f\n", GREEN, RESET, v1, p1Str, x, v2, p2Str, y);
        } else {
            System.out.println("\n" + RED + "El sistema es inconsistente." + RESET);
        }
    }

    public static void main(String[] args) {
        mostrarMenu();
    }

    static void mostrarMenu() {
        while (true) {
            System.out.print(CLEAR);
            System.out.flush();
            System.out.println(BOLD + CYAN + "=== MENÚ PRINCIPAL ===" + RESET);
            System.out.println("1. Resolver Bisección Estándar");
            System.out.println("2. Resolver Bisección de FÍSICA");
            System.out.println("3. Resolver Sistema de Ecuaciones 2x2");
            System.out.println("4. Salir");

            String opcion = IO.leerLinea("\n" + YELLOW + "Elige una opción: " + RESET);

            if (opcion.equals("1")) {
                metodoBiseccionPolinomios();
            } else if (opcion.equals("2")) {
                metodoBiseccionFisica();
            } else if (opcion.equals("3")) {
                sistemaDeEcuacion();
                IO.leerLinea("\nPresiona ENTER para continuar...");
            } else if (opcion.equals("4")) {
                System.out.println(GREEN + "Saliendo..." + RESET);
                break;
            } else {
                System.out.println(RED + "Opción inválida. Intenta de nuevo." + RESET);
                try { Thread.sleep(1500); } catch (InterruptedException ignore) {}
            }
        }
    }

    // Evaluador matemático para polinomios estándar
    static double fPolinomio(double[] coef, int grado, double x) {
        double sum = 0;
        for (int i = 0; i <= grado; i++) {
            sum += coef[i] * Math.pow(x, grado - i);
        }
        return sum;
    }

    // Evaluador matemático específico para los problemas de física
    static double fFisica(double h0, double v0, double t) {
        return h0 + (v0 * t) - (4.905 * Math.pow(t, 2));
    }

    static void metodoBiseccionFisica() {
        System.out.print(CLEAR);
        System.out.flush();
        System.out.println(BOLD + CYAN + "<h> Bisección: Cinemática </h>" + RESET);
        System.out.println(CYAN + "Ecuación base: f(t) = AlturaInicial + VelocidadInicial(t) - 4.905(t^2)" + RESET);

        double h0 = 0, v0 = 0, es = 0;

        while (true) {
            try {
                System.out.println();
                h0 = Double.parseDouble(IO.leerLinea(YELLOW + "Ingresa la Altura Inicial (ej. 10): " + RESET));
                v0 = Double.parseDouble(IO.leerLinea(YELLOW + "Ingresa la Velocidad Inicial (Si es caída libre, pon 0): " + RESET));
                es = Double.parseDouble(IO.leerLinea(YELLOW + "Ingresa el error esperado (Es) en % (ej. 1): " + RESET));
                break;
            } catch (Exception e) {
                System.out.println(RED + "Valor inválido. Asegúrate de ingresar solo números." + RESET);
            }
        }

        System.out.println(GREEN + "\nEcuación configurada: f(t) = " + h0 + " + " + v0 + "t - 4.905t^2" + RESET);

        double xi = 0, xu = 0;
        while (true) {
            try {
                System.out.println(CYAN + "\n--- Evaluación de Límites de Tiempo ---" + RESET);
                xi = Double.parseDouble(IO.leerLinea(YELLOW + "Ingresa Xi (tiempo inferior): " + RESET));
                xu = Double.parseDouble(IO.leerLinea(YELLOW + "Ingresa Xu (tiempo superior): " + RESET));

                double fxi_test = fFisica(h0, v0, xi);
                double fxu_test = fFisica(h0, v0, xu);

                if (fxi_test * fxu_test > 0) {
                    System.out.println(RED + "\n¡Advertencia! f(xi)*f(xu) > 0. Para que exista raíz uno debe ser positivo y otro negativo." + RESET);
                    String forzar = IO.leerLinea(YELLOW + "¿Deseas forzar la ejecución para imprimir la tabla de todos modos? (s/n): " + RESET);
                    if (!forzar.equalsIgnoreCase("s")) {
                        continue;
                    }
                }
                break;
            } catch (Exception e) {
                System.out.println(RED + "Valor inválido. Intenta de nuevo." + RESET);
            }
        }

        System.out.println("\nGenerando tabla...\n");
        System.out.println("+----+----------+----------+----------+----------+----------+--------------+----------+");
        System.out.println("| It |    Xi    |    Xu    |    Xr    |  f(xi)   |  f(xr)   |  f(xi)f(xr)  |   Ea %   |");
        System.out.println("+----+----------+----------+----------+----------+----------+--------------+----------+");

        int iter = 1;
        double xr = 0, xr_old = 0, ea = 100;

        while (true) {
            xr = (xi + xu) / 2.0;
            double fxi = fFisica(h0, v0, xi);
            double fxr = fFisica(h0, v0, xr);
            double fxifxr = fxi * fxr;

            if (iter > 1) {
                ea = Math.abs((xr - xr_old) / xr) * 100.0;
            }

            String eaStr = (iter == 1) ? "  ---   " : String.format("%8.2f", ea);

            System.out.printf("| %2d | %8.4f | %8.4f | %8.4f | %8.4f | %8.4f | %12.4f | %8s |\n",
                    iter, xi, xu, xr, fxi, fxr, fxifxr, eaStr);

            if (iter > 1 && ea <= es) {
                break;
            }

            if (fxifxr < 0) {
                xu = xr;
            } else if (fxifxr > 0) {
                xi = xr;
            } else {
                ea = 0;
                break;
            }

            xr_old = xr;
            iter++;

            if (iter > 50) {
                System.out.println("| ...| Límite de 50 iteraciones alcanzado. |");
                break;
            }
        }
        System.out.println("+----+----------+----------+----------+----------+----------+--------------+----------+");
        System.out.printf("\n%sLa piedra/balón toca el suelo aprox. a los %.4f segundos.%s\n", GREEN, xr, RESET);

        IO.leerLinea("\nPresiona ENTER para continuar...");
    }

    // El método estándar para polinomios que ya teníamos
    static void metodoBiseccionPolinomios() {
        System.out.print(CLEAR);
        System.out.flush();
        System.out.println(BOLD + CYAN + "<h> Método de Bisección Estándar </h>" + RESET);

        boolean configurarNuevaEcuacion = true;

        if (coeficientesMemoria != null) {
            System.out.println(GREEN + "Hay una ecuación guardada en memoria." + RESET);
            String respuesta = IO.leerLinea(YELLOW + "¿Deseas ingresar un NUEVO problema? (s/n): " + RESET);
            if (!respuesta.equalsIgnoreCase("s")) {
                configurarNuevaEcuacion = false;
            }
        }

        if (configurarNuevaEcuacion) {
            System.out.println(CYAN + "\n--- Configuración de la Función ---" + RESET);
            while (true) {
                try {
                    gradoMemoria = Integer.parseInt(IO.leerLinea(YELLOW + "Ingresa el GRADO del polinomio (ej. 2 para cuadrática): " + RESET));
                    if (gradoMemoria >= 1) break;
                    System.out.println(RED + "El grado debe ser al menos 1." + RESET);
                } catch (Exception e) {
                    System.out.println(RED + "Por favor, ingresa un número entero." + RESET);
                }
            }

            coeficientesMemoria = new double[gradoMemoria + 1];
            System.out.println(YELLOW + "Ecuación de la forma: a*x^n + b*x^(n-1) + ... = 0" + RESET);
            for (int i = 0; i <= gradoMemoria; i++) {
                while (true) {
                    try {
                        coeficientesMemoria[i] = Double.parseDouble(IO.leerLinea("Coeficiente de x^" + (gradoMemoria - i) + ": "));
                        break;
                    } catch (Exception e) {
                        System.out.println(RED + "Valor inválido. Ingresa un número." + RESET);
                    }
                }
            }

            while (true) {
                try {
                    esMemoria = Double.parseDouble(IO.leerLinea(YELLOW + "Ingresa el error esperado (Es) en % (ej. 1.5): " + RESET));
                    break;
                } catch (Exception e) {
                    System.out.println(RED + "Valor inválido. Ingresa un número." + RESET);
                }
            }
            System.out.println(GREEN + ">> Ecuación guardada correctamente.\n" + RESET);
        }

        double xi = 0, xu = 0;
        while (true) {
            try {
                System.out.println(CYAN + "--- Evaluación de Límites ---" + RESET);
                xi = Double.parseDouble(IO.leerLinea(YELLOW + "Ingresa el valor de Xi: " + RESET));
                xu = Double.parseDouble(IO.leerLinea(YELLOW + "Ingresa el valor de Xu: " + RESET));

                double fxi_test = fPolinomio(coeficientesMemoria, gradoMemoria, xi);
                double fxu_test = fPolinomio(coeficientesMemoria, gradoMemoria, xu);

                if (fxi_test * fxu_test > 0) {
                    System.out.println(RED + "\n¡Nota! f(xi)*f(xu) > 0. (Uno debe ser positivo y otro negativo)." + RESET);
                    String forzar = IO.leerLinea(YELLOW + "¿Deseas forzar la ejecución para imprimir la tabla? (s/n): " + RESET);
                    if (!forzar.equalsIgnoreCase("s")) {
                        continue;
                    }
                }
                break;
            } catch (Exception e) {
                System.out.println(RED + "Valor inválido. Intenta de nuevo." + RESET);
            }
        }

        System.out.println("\nGenerando tabla...\n");
        System.out.println("+----+----------+----------+----------+----------+----------+--------------+----------+");
        System.out.println("| It |    Xi    |    Xu    |    Xr    |  f(xi)   |  f(xr)   |  f(xi)f(xr)  |   Ea %   |");
        System.out.println("+----+----------+----------+----------+----------+----------+--------------+----------+");

        int iter = 1;
        double xr = 0, xr_old = 0, ea = 100;

        while (true) {
            xr = (xi + xu) / 2.0;
            double fxi = fPolinomio(coeficientesMemoria, gradoMemoria, xi);
            double fxr = fPolinomio(coeficientesMemoria, gradoMemoria, xr);
            double fxifxr = fxi * fxr;

            if (iter > 1) {
                ea = Math.abs((xr - xr_old) / xr) * 100.0;
            }

            String eaStr = (iter == 1) ? "  ---   " : String.format("%8.2f", ea);

            System.out.printf("| %2d | %8.4f | %8.4f | %8.4f | %8.4f | %8.4f | %12.4f | %8s |\n",
                    iter, xi, xu, xr, fxi, fxr, fxifxr, eaStr);

            if (iter > 1 && ea <= esMemoria) {
                break;
            }

            if (fxifxr < 0) {
                xu = xr;
            } else if (fxifxr > 0) {
                xi = xr;
            } else {
                ea = 0;
                break;
            }

            xr_old = xr;
            iter++;

            if (iter > 50) {
                System.out.println("| ...| Límite de 50 iteraciones alcanzado. |");
                break;
            }
        }
        System.out.println("+----+----------+----------+----------+----------+----------+--------------+----------+");

        System.out.printf("\n%sProceso terminado.%s\n", GREEN, RESET);

        IO.leerLinea("\nPresiona ENTER para continuar...");
    }
}

class IO {
    private static final Scanner scanner = new Scanner(System.in);
    static String leerLinea(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}