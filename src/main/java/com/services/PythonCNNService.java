package com.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PythonCNNService {
    private static final String PYTHON_SCRIPT_PATH = "C:/Users/sjoha/Downloads/Cachamas/pythonscript.py";
    private static final String PYTHON_EXECUTABLE = "python"; // Cambia esto si es necesario

    public static void main(String[] args) {
        String imagePath = "C:/Users/sjoha/Downloads/Cachamas/cuerpo/imagen.jpg"; // Cambia esto a la ruta de tu imagen
        String result = runPythonScript(imagePath);
        System.out.println("Resultado del script: " + result);
    }

    public static String runPythonScript(String imagePath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(PYTHON_EXECUTABLE, PYTHON_SCRIPT_PATH, imagePath);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return output.toString();
            } else {
                return "Error al ejecutar el script. Código de salida: " + exitCode;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
