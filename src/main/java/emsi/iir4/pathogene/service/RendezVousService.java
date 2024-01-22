package emsi.iir4.pathogene.service;

import emsi.iir4.pathogene.domain.Medecin;
import emsi.iir4.pathogene.domain.RendezVous;
import emsi.iir4.pathogene.repository.RendezVousRepository;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RendezVousService {

    @Autowired
    RendezVousRepository rendezVousRepository;

    public List<RendezVous> getRendezVousByMedecinAndDate(Long medecinId, LocalDate date) {
        List<RendezVous> allByDate = new ArrayList<>();
        List<RendezVous> rendezVous = rendezVousRepository.findAllByMedecin_Id(medecinId);

        for (RendezVous r : rendezVous) {
            if (r.getDate().toLocalDate().equals(date)) {
                allByDate.add(r);
            }
        }

        return allByDate;
    }

    public List<LocalTime> getReservedTimesByMedecinAndDate(Long medecinId, LocalDate date) {
        List<LocalTime> reservedTimes = new ArrayList<>();
        List<RendezVous> rendezVous = rendezVousRepository.findAllByMedecin_Id(medecinId);

        for (RendezVous r : rendezVous) {
            if (r.getDate().toLocalDate().equals(date)) {
                // Extrait l'heure sans décalage horaire
                LocalTime localTime = r.getDate().toLocalTime();

                reservedTimes.add(localTime);
            }
        }

        return reservedTimes;
    }

    public List<String> getHeuresDisponiblesPourMedecin(LocalDate date, Long id) {
        List<LocalTime> heuresReservees = getReservedTimesByMedecinAndDate(id, date);

        List<String> heuresDisponibles = Arrays.asList(
            "08:30",
            "08:45",
            "09:00",
            "09:15",
            "09:30",
            "09:45",
            "10:00",
            "10:15",
            "10:30",
            "10:45",
            "11:00",
            "11:15",
            "11:30",
            "11:45",
            "12:00",
            "12:15",
            "12:30",
            "12:45",
            "13:00",
            "13:15",
            "13:30",
            "13:45",
            "14:00",
            "14:15",
            "14:30",
            "14:45",
            "15:00",
            "15:15",
            "15:30",
            "15:45",
            "16:00",
            "16:15",
            "16:30",
            "16:45",
            "17:00",
            "17:15",
            "17:30",
            "17:45",
            "18:00"
        );

        return heuresDisponibles.stream().filter(heure -> !heuresReservees.contains(LocalTime.parse(heure))).collect(Collectors.toList());
    }
    // Add this new method to check if Medecin is already booked at the specified time

}
