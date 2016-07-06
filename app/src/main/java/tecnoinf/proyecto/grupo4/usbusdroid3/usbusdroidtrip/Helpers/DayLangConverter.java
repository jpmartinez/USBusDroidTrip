package tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Helpers;

import tecnoinf.proyecto.grupo4.usbusdroid3.usbusdroidtrip.Models.DayOfWeek;

/**
 * Created by Kavesa on 02/06/16.
 */
public class DayLangConverter {

    public static String convertToES(DayOfWeek day){
        switch (day){
            case MONDAY:    return "LUNES";
            case TUESDAY:   return "MARTES";
            case WEDNESDAY: return "MIÉRCOLES";
            case FRIDAY:    return "VIERNES";
            case THURSDAY:  return "JUEVES";
            case SATURDAY:  return "SÁBADO";
            case SUNDAY:    return "DOMINGO";
            default:        return null;
        }
    }

    public static String convertToEN(String day){
        switch (day){
            case "LUNES":    return "MONDAY";
            case "MARTES":   return "TUESDAY";
            case "MIÉRCOLES":return "WEDNESDAY";
            case "MIERCOLES":return "WEDNESDAY";
            case "JUEVES":   return "THURSDAY";
            case "VIERNES":  return "FRIDAY";
            case "SÁBADO":   return "SATURDAY";
            case "SABADO":   return "SATURDAY";
            case "DOMINGO":  return "SUNDAY";
            default:         return null;
        }
    }
}
