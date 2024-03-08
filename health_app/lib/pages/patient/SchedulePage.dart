import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:health_app/models/AppointmentModel.dart';
import 'package:health_app/services/api_service.dart';
import 'package:health_app/theme/colors.dart';
import 'package:health_app/theme/styles.dart' as styles;
import 'package:intl/intl.dart';

class SchedulePage extends StatefulWidget {
  @override
  State<SchedulePage> createState() => _SchedulePageState();
}

enum FilterStatus { being_processed, accepted, refused }

class _SchedulePageState extends State<SchedulePage> {
  FilterStatus status = FilterStatus.being_processed;
  Alignment _alignment = Alignment.centerLeft;
  ApiService apiService = ApiService();
  List<AppointmentModel>? appointments;
  List<AppointmentModel>? filteredSchedules;
  var rendezVousPatient;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance?.addPostFrameCallback((_) async {
      if (mounted) {
        await fetchAppointments();
      }
    });
  }

  Future<void> fetchAppointments() async {
    var res = await apiService.getRendezVousByPatient();
    rendezVousPatient = json.decode(res.body);

    setState(() {
      appointments = (rendezVousPatient as List)
          .map((appointment) => AppointmentModel.fromJson(appointment))
          .toList();
    });
  }

  @override
  Widget build(BuildContext context) {
    filteredSchedules = appointments
        ?.where((appointment) => appointment.status == status.toString().split('.').last)
        .toList();

    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 30, vertical: 30),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            SizedBox(height: 20),
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
                                status = filterStatus;
                                if (filterStatus == FilterStatus.being_processed) {
                                  _alignment = Alignment.centerLeft;
                                } else if (filterStatus == FilterStatus.accepted) {
                                  _alignment = Alignment.center;
                                } else if (filterStatus == FilterStatus.refused) {
                                  _alignment = Alignment.centerRight;
                                }
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
                  duration: const Duration(milliseconds: 200),
                  alignment: _alignment,
                  child: Container(
                    width: 105,
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
            SizedBox(height: 20),
            Expanded(
              child: (filteredSchedules?.isEmpty ?? true)
                  ? Center(
                child: Text(
                  'Data not loaded yet',
                  style: TextStyle(
                    fontSize: 16,
                    color: Colors.black,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              )
                  : ListView.builder(
                itemCount: filteredSchedules!.length,
                itemBuilder: (context, index) {
                  var schedule = filteredSchedules![index];
                  bool isLastElement = filteredSchedules!.length == index + 1;

                  return Card(
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(30.0),
                    ),
                    margin: !isLastElement ? EdgeInsets.only(bottom: 20) : EdgeInsets.zero,
                    child: Padding(
                      padding: EdgeInsets.all(15),
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
                                                _alignment = Alignment.center;
                                              } else if (filterStatus ==
                                                  FilterStatus.refused) {
                                                status = FilterStatus.refused;
                                                _alignment = Alignment.centerRight;
                                              }
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
                                duration: const Duration(milliseconds: 200),
                                alignment: _alignment,
                                child: Container(
                                  width: 105,
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
                                ? Center(
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

                                  decoration: BoxDecoration(
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
                                                    base64Decode(schedule!.medecin!.photo)),
                                              ),
                                              SizedBox(
                                                width: 10,
                                              ),
                                              Column(
                                                crossAxisAlignment: CrossAxisAlignment.start,
                                                children: [
                                                  Text(
                                                    'Dr ${schedule.medecin!.nom}',
                                                    // _schedule['doctorName'],
                                                    style: TextStyle(
                                                      color: Color(MyColors.header01),
                                                      fontWeight: FontWeight.w700,
                                                    ),
                                                  ),
                                                  SizedBox(
                                                    height: 5,
                                                  ),
                                                  Text(
                                                    schedule.medecin!.type,
                                                    // _schedule['doctorTitle'],
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
                },
              ),
            )
          ],
        ),
      ),
    );
  }
}
