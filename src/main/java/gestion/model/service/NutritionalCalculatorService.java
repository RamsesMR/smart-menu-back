package gestion.model.service;

import gestion.model.collections.DTO.RecommendationRequest;
import gestion.model.enums.GoalType;
import gestion.model.enums.Sexo;
import org.springframework.stereotype.Service;

/**
 * Calcula las calorías objetivo por comida usando Mifflin-St Jeor.
 * Extraído de RecommendationService para respetar el SRP.
 */
@Service
public class NutritionalCalculatorService {

    private static final double DEFAULT_PESO_KG   = 70.0;
    private static final int    DEFAULT_ALTURA_CM = 170;
    private static final int    DEFAULT_EDAD      = 30;
    private static final double FACTOR_ACTIVIDAD  = 1.55;
    private static final int    COMIDAS_POR_DIA   = 3;

    public int estimarKcalPorComida(RecommendationRequest req) {
        double peso   = req.getPesoKg()   != null ? req.getPesoKg()   : DEFAULT_PESO_KG;
        int    altura = req.getAlturaCm() != null ? req.getAlturaCm() : DEFAULT_ALTURA_CM;
        int    edad   = req.getEdad()     != null ? req.getEdad()     : DEFAULT_EDAD;

        double tmb        = calcularTmb(peso, altura, edad, req.getSexo());
        double tdee       = tmb * FACTOR_ACTIVIDAD;
        double kcalComida = tdee / COMIDAS_POR_DIA;

        kcalComida = ajustarSegunObjetivo(kcalComida, req.getObjetivo());
        return (int) Math.round(kcalComida);
    }

    private double calcularTmb(double pesoKg, int alturaCm, int edad, Sexo sexo) {
        double base = 10 * pesoKg + 6.25 * alturaCm - 5.0 * edad;
        return (sexo == Sexo.MUJER) ? base - 161 : base + 5;
    }

    private double ajustarSegunObjetivo(double kcal, GoalType objetivo) {
        if (objetivo == null) return kcal;
        return switch (objetivo) {
            case PERDER_PESO   -> kcal * 0.80;
            case GANAR_MUSCULO -> kcal * 1.15;
            case MANTENER      -> kcal;
        };
    }
}