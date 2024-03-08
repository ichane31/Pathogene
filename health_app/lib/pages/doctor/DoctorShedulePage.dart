import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:health_app/models/AppointmentModel.dart';
import 'package:health_app/models/doctor_model.dart';
import 'package:health_app/services/api_service.dart';

import 'package:health_app/theme/colors.dart';
import 'package:health_app/theme/styles.dart' as styles;
import 'package:intl/intl.dart';

class DoctorSchedulePage extends StatefulWidget {
  // const SchedulePage({Key? key}) : super(key: key);

  @override
  State<DoctorSchedulePage> createState() => _DoctorSchedulePageState();
}

enum FilterStatus { being_processed, accepted }

/* List<Map> schedules = [
  {
    'img': 'assets/doctor01.jpeg',
    'doctorName': 'Dr. Anastasya Syahid',
    'doctorTitle': 'Dental Specialist',
    'reservedDate': 'Monday, Aug 29',
    'reservedTime': '11:00 - 12:00',
    'status': FilterStatus.Upcoming
  },
  {
    'img': 'assets/doctor02.png',
    'doctorName': 'Dr. Mauldya Imran',
    'doctorTitle': 'Skin Specialist',
    'reservedDate': 'Monday, Sep 29',
    'reservedTime': '11:00 - 12:00',
    'status': FilterStatus.Upcoming
  },
  {
    'img': 'assets/doctor03.jpeg',
    'doctorName': 'Dr. Rihanna Garland',
    'doctorTitle': 'General Specialist',
    'reservedDate': 'Monday, Jul 29',
    'reservedTime': '11:00 - 12:00',
    'status': FilterStatus.Upcoming
  },
  {
    'img': 'assets/doctor04.jpeg',
    'doctorName': 'Dr. John Doe',
    'doctorTitle': 'Something Specialist',
    'reservedDate': 'Monday, Jul 29',
    'reservedTime': '11:00 - 12:00',
    'status': FilterStatus.Complete
  },
  {
    'img': 'assets/doctor05.jpeg',
    'doctorName': 'Dr. Sam Smithh',
    'doctorTitle': 'Other Specialist',
    'reservedDate': 'Monday, Jul 29',
    'reservedTime': '11:00 - 12:00',
    'status': FilterStatus.Cancel
  },
  {
    'img': 'assets/doctor05.jpeg',
    'doctorName': 'Dr. Sam Smithh',
    'doctorTitle': 'Other Specialist',
    'reservedDate': 'Monday, Jul 29',
    'reservedTime': '11:00 - 12:00',
    'status': FilterStatus.Cancel
  },
]; */

class _DoctorSchedulePageState extends State<DoctorSchedulePage> {
  FilterStatus status = FilterStatus.being_processed;
  Alignment _alignment = Alignment.centerLeft;
  ApiService apiService = new ApiService();
  List<AppointmentModel>? AppointmentDataList;
  var _isVisible = false;
  List<AppointmentModel>? Appointments;
  List<AppointmentModel>? filteredSchedules;
  var RendezVousMedecin ;
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    WidgetsBinding.instance?.addPostFrameCallback((_) async {
      if (mounted) {
        getAppointmentByMedecin().then((RendezVousMedecin) {
          if (mounted) {
            setState(() {
              Appointments = (RendezVousMedecin as List)
                  .map((appointment) => AppointmentModel.fromJson(appointment))
                  .toList();
            });
          }
        });
      }
    });

  }

  void iterateJson(String jsonStr) {
    Map<String, dynamic> myMap = json.decode(jsonStr);
    List<dynamic> entitlements = myMap["Dependents"][0]["Entitlements"];
    entitlements.forEach((entitlement) {
      (entitlement as Map<String, dynamic>).forEach((key, value) {
        print(key);
        (value as Map<String, dynamic>).forEach((key2, value2) {
          print(key2);
          print(value2);
        });
      });
    });
  }

  Future getDoctorOfUser() async {
    //get Rdvs of this Patient
    var res = await apiService.getDoctorOfUser();
    print("aaaaaaaaaaaaaa ${res.statusCode}");
    var DoctorOfUser = json.decode(res.body);
    print(DoctorOfUser);
    return DoctorOfUser;
  }

  Future getAppointmentByMedecin() async {
    var res = await apiService.getRendezVousByMedecin();
    RendezVousMedecin = json.decode(res.body);
    print(res.statusCode);
    return RendezVousMedecin;
  }

  @override
  Widget build(BuildContext context) {
    /*
    filteredSchedules = Appointments!.where((var appointment) {
      print("appointment.status : ${appointment.status}");
      print("status.toString() : ${status.toString().split('.').last}");
      return appointment.status == status.toString().split('.').last;
    }).toList();*/

    /*
    if (Appointments == null) {
      // Handle the case where Appointments is null
      return CircularProgressIndicator(); // or show an error message
    }
     */

    if (Appointments != null) {
      filteredSchedules = Appointments!.where((appointment) {
        print("appointment.status : ${appointment.status}");
        print("status.toString() : ${status.toString().split('.').last}");
        return appointment.status == status.toString().split('.').last;
      }).toList();
    } else {
      // Gérer le cas où Appointments est null, par exemple, en initialisant filteredSchedules à une valeur par défaut.
      filteredSchedules = [];
    }

    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.only(left: 30, top: 30, right: 30),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            SizedBox(
              height: 20,
            ),
            Stack(
              children: [
                Container(
                  width: double.infinity,
                  height: 40,
                  decoration: BoxDecoration(
                    color: Color(MyColors.bg),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      for (FilterStatus filterStatus in FilterStatus.values)
                        Expanded(
                          child: GestureDetector(
                            onTap: () {
                              setState(() {
                                if (filterStatus ==
                                    FilterStatus.being_processed) {
                                  status = FilterStatus.being_processed;
                                  _alignment = Alignment.centerLeft;
                                } else if (filterStatus ==
                                    FilterStatus.accepted) {
                                  status = FilterStatus.accepted;
                                  _alignment = Alignment.centerRight;
                                } /*else if (filterStatus ==
                                    FilterStatus.refused) {
                                  status = FilterStatus.refused;
                                  _alignment = Alignment.centerRight;
                                }*/
                              });
                            },
                            child: Center(
                              child: Text(
                                filterStatus.name,
                                style: styles.kFilterStyle,
                              ),
                            ),
                          ),
                        ),
                    ],
                  ),
                ),
                AnimatedAlign(
                  duration: Duration(milliseconds: 150),
                  alignment: _alignment,
                  child: Container(
                    width: 140,
                    height: 40,
                    decoration: BoxDecoration(
                      color: Color(MyColors.primary),
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Center(
                      child: Text(
                        status.name,
                        style: TextStyle(
                          color: Colors.white,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ),
                )
              ],
            ),
            SizedBox(
              height: 20,
            ),
            Expanded(
              child: (filteredSchedules!.isEmpty)
                  ? const Center(
                child: Text('data not loaded yet',
                    style: TextStyle(
                      fontSize: 16,  // Vous pouvez ajuster la taille de la police ici
                      color: Colors.black,  // Vous pouvez ajuster la couleur du texte ici
                      fontWeight: FontWeight.bold,  // Vous pouvez ajuster le poids de la police ici
                    )),
              )
                  :ListView.builder(
                itemCount: filteredSchedules?.length,
                //itemCount: filteredSchedules.length,
                itemBuilder: (context, index) {
                  var schedule = filteredSchedules?[index];
                  bool isLastElement = ((filteredSchedules?.length != null)
                      ? filteredSchedules?.length
                      : 3)! +
                      1 ==
                      index;
                  return Container(

                    decoration: const BoxDecoration(
                      shape: BoxShape.circle,
                      // borderRadius: BorderRadius.all(Radius.circular(80.0)),
                    ),
                    child: Card(
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(30.0),
                      ),
                      margin: !isLastElement
                          ? EdgeInsets.only(bottom: 20)
                          : EdgeInsets.zero,
                      child: Padding(
                        padding: EdgeInsets.all(15),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.stretch,
                          children: [
                            Row(
                              children: [
                                CircleAvatar(
                                  // backgroundImage: AssetImage(_schedule['img']),
                                  child: Image.memory(
                                      base64Decode(schedule!.patient!.photo ?? '')
                                  ),
                                ),
                                const SizedBox(
                                  width: 10,
                                ),
                                Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text(
                                      'P ${schedule.patient!.nom}',
                                      // _schedule['patientName'],
                                      style: TextStyle(
                                        color: Color(MyColors.header01),
                                        fontWeight: FontWeight.w700,
                                      ),
                                    ),
                                    SizedBox(
                                      height: 5,
                                    ),
                                    Text(
                                      'Tel : ${schedule.patient!.telephone ?? ''}',
                                      // _schedule['patientGenre'],
                                      style: TextStyle(
                                        color: Color(MyColors.grey02),
                                        fontSize: 12,
                                        fontWeight: FontWeight.w600,
                                      ),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                            SizedBox(
                              height: 15,
                            ),
                            Container(
                              decoration: BoxDecoration(
                                color: Color(MyColors.bg03),
                                borderRadius: BorderRadius.circular(10),
                              ),
                              width: double.infinity,
                              padding: EdgeInsets.all(15),
                              child: Row(
                                crossAxisAlignment: CrossAxisAlignment.center,
                                mainAxisAlignment:
                                MainAxisAlignment.spaceBetween,
                                children: [
                                  Row(
                                    children: [
                                      Icon(
                                        Icons.calendar_today,
                                        color: Color(MyColors.primary),
                                        size: 15,
                                      ),
                                      SizedBox(
                                        width: 5,
                                      ),
                                      Text(
                                        DateFormat.yMMMMEEEEd()
                                            .format(schedule.date!),
                                        style: TextStyle(
                                          fontSize: 12,
                                          color: Color(MyColors.primary),
                                          fontWeight: FontWeight.bold,
                                        ),
                                      ),
                                    ],
                                  ),
                                  Row(
                                    children: [
                                      Icon(
                                        Icons.access_alarm,
                                        color: Color(MyColors.primary),
                                        size: 17,
                                      ),
                                      SizedBox(
                                        width: 5,
                                      ),
                                      Text(
                                        DateFormat.jm().format(schedule.date!),
                                        style: TextStyle(
                                          color: Color(MyColors.primary),
                                          fontWeight: FontWeight.bold,
                                        ),
                                      ),
                                    ],
                                  )
                                ],
                              ),
                            ),
                            //DateTimeCard(schedule: schedule),
                            SizedBox(
                              height: 15,
                            ),
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Expanded(
                                  child: OutlinedButton(
                                    child: Text('Cancel'),
                                    onPressed: () {},
                                  ),
                                ),
                                SizedBox(
                                  width: 20,
                                ),
                                Expanded(
                                  child: ElevatedButton(
                                    child: Text('Reschedule'),
                                    onPressed: () => {},
                                  ),
                                )
                              ],
                            )
                          ],
                        ),
                      ),
                    ),
                  );
                },
              ),
            )
          ],
        ),
      ),
    );
  }
}

/* class DateTimeCard extends StatelessWidget {
  const DateTimeCard({
    Key? key,
    required AppointmentModel schedule,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Color(MyColors.bg03),
        borderRadius: BorderRadius.circular(20),
      ),
      width: double.infinity,
      padding: EdgeInsets.all(10),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Row(
            children: [
              Icon(
                Icons.calendar_today,
                color: Color(MyColors.primary),
                size: 15,
              ),
              SizedBox(
                width: 5,
              ),
              Text(
                DateFormat.yMd().add_jm().format() ,
                style: TextStyle(
                  fontSize: 12,
                  color: Color(MyColors.primary),
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          Row(
            children: [
              Icon(
                Icons.access_alarm,
                color: Color(MyColors.primary),
                size: 17,
              ),
              SizedBox(
                width: 5,
              ),
              Text(
                '11:00 ~ 12:10',
                style: TextStyle(
                  color: Color(MyColors.primary),
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          )
        ],
      ),
    );
  }
} */
