import 'dart:convert';
import 'dart:math';
import 'package:flutter/material.dart';
import 'package:health_app/helpers/constant.dart';
import 'package:health_app/models/AppointmentModel.dart';
import 'package:health_app/pages/doctor/appointmentDetailsPage.dart';
import 'package:health_app/pages/patient/components/header_with_searchBox.dart';

import 'package:health_app/services/api_service.dart';
import 'package:health_app/theme/extention.dart';
import 'package:intl/intl.dart';

//class Body extends StatelessWidget {
class BodyDoctor extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _BodyDoctorState();
}

class _BodyDoctorState extends State<BodyDoctor> {
  List<AppointmentModel>? appointmentDataList;
  Image? image;
  ApiService apiService = new ApiService();
  var Appointmentsdata;
  bool _isVisible = false;
  String searchName = ''; // Add a variable to store the search term

  static var userData;
  @override
  void initState() {
    super.initState();
    getAppointments().then((Appointmentsdata) {
      setState(() {
        if(Appointmentsdata != null) {
          appointmentDataList =
              (Appointmentsdata as List<dynamic>)
                  .map((x) => AppointmentModel.fromJson(x))
                  .toList();
          _isVisible = !_isVisible;
        }
        else {
          appointmentDataList  = [];
        }

      });
    });

  }


  Future getActualUser() async {
    var res = await apiService.getMyProfile();
    userData = json.decode(res.body);

    return userData;
  }

  Future getAppointments() async {
    var res = await apiService.getRendezVousByMedecin();
    Appointmentsdata = json.decode(res.body);
    return Appointmentsdata;
  }

  void delay() async {
    await Future.delayed(const Duration(seconds: 10), () {});
  }

  Color randomColor() {
    var random = Random();
    final colorList = [
      Theme.of(context).primaryColor,
      LightColor.orange,
      LightColor.green,
      LightColor.grey,
      LightColor.lightOrange,
      LightColor.skyBlue,
      LightColor.titleTextColor,
      Colors.red,
      Colors.brown,
      LightColor.purpleExtraLight,
      LightColor.skyBlue,
    ];
    var color = colorList[random.nextInt(colorList.length)];
    return color;
  }


  @override
  Widget build(BuildContext context) {
    // It will provie us total height  and width of our screen
    Size size = MediaQuery.of(context).size;
    // it enable scrolling on small device
    return SingleChildScrollView(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          HeaderWithSearchBox(
            size: size,
            onSearchChanged: (value) {
              setState(() {
                searchName = value; // Update the search term
              });
            },
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: <Widget>[
              Text("Appointment of Patients", style: TextStyles.title.bold),
              IconButton(
                  icon: Icon(
                    Icons.sort,
                    color: Theme.of(context).primaryColor,
                  ),
                  onPressed: () {})
            ],
          ).hP16,
          SizedBox(
            height: 20,
          ),
          getAppointmentsWidgetList(),
        ],
      ),
    );
  }

  Widget getAppointmentsWidgetList() {
    return Visibility(
      visible: _isVisible,
      child: Column(
        children: (appointmentDataList != null)
            ? appointmentDataList!
            .where((x) =>
        x.status == "accepted" &&
            (x.patient?.nom
                ?.toLowerCase()
                .contains(searchName.toLowerCase()) ??
                false) ||
            (x.patient?.prenom
                ?.toLowerCase()
                .contains(searchName.toLowerCase()) ??
                false))
            .map((x) {
          return _appointmentTile(x);
        }).toList()
            : [
          const Center(
            child: Text(
              'Data not loaded yet please wait',
              style: TextStyle(
                fontSize: 16,
                color: Colors.black,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        ],
      ),
    );
  }
  Widget _appointmentTile(AppointmentModel model) {
    return Container(
      margin: EdgeInsets.symmetric(vertical: 8, horizontal: 16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.all(Radius.circular(20)),
        boxShadow: <BoxShadow>[
          BoxShadow(
            offset: Offset(4, 4),
            blurRadius: 10,
            color: LightColor.grey.withOpacity(.2),
          ),
          BoxShadow(
            offset: Offset(-3, 0),
            blurRadius: 15,
            color: LightColor.grey.withOpacity(.1),
          )
        ],
      ),
      child: Container(
        padding: EdgeInsets.symmetric(horizontal: 18, vertical: 8),
        child: ListTile(
          contentPadding: EdgeInsets.all(0),
          leading: ClipRRect(
            borderRadius: BorderRadius.all(Radius.circular(13)),
            child: Container(
              height: 55,
              width: 55,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(15),
                color: Color(0xFF417ee4),
              ),
              child: Image.memory(
                base64Decode(model.patient!.photo!),
                height: 50,
                width: 50,
                fit: BoxFit.contain,
              ),
            ),
          ),
          title: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                "Patient: ${model.patient!.nom} ${model.patient!.prenom}",
                style: TextStyles.title.bold,
              ),
              Text(
                "Date: ${_formatDateTime(model.date!)}",
                style: TextStyles.bodySm.subTitleColor.bold,
              ),
            ],
          ),
          trailing: Icon(
            Icons.keyboard_arrow_right,
            size: 30,
            color: Theme.of(context).primaryColor,
          ),
        ),
      ).ripple(
            () {
          Navigator.of(context).push(MaterialPageRoute(
            builder: (_) => AppointmentDetailsPage(model: model),
          ));
        },
        borderRadius: const BorderRadius.all(Radius.circular(20)),
      ),
    );
  }

// Add this method to format DateTime
  String _formatDateTime(DateTime dateTime) {
    final formatter = DateFormat('dd/MM/yyyy HH:mm');
    return formatter.format(dateTime);
  }

}
