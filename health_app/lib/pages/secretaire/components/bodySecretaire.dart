import 'dart:convert';
import 'dart:math';
import 'package:flutter/material.dart';
import 'package:health_app/helpers/constant.dart';
import 'package:health_app/models/AppointmentModel.dart';
import 'package:health_app/pages/secretaire/AppointmentDetailsPage.dart';
import 'package:health_app/services/api_service.dart';
import 'package:health_app/theme/extention.dart';
import 'package:intl/intl.dart';

import '../../patient/components/header_with_searchBox.dart';

class BodySecretaire extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _BodySecretaireState();
}

class _BodySecretaireState extends State<BodySecretaire> {
  List<AppointmentModel>? appointmentDataList;
  List<AppointmentModel>? filteredAppointments;
  ApiService apiService = new ApiService();
  bool _isVisible = false;
  TextEditingController searchController = TextEditingController();

  @override
  void initState() {
    super.initState();
    getAppointments();
  }

  Future<void> getAppointments() async {
    var res = await apiService.getAppointments();
    setState(() {
      appointmentDataList = (json.decode(res.body) as List<dynamic>)
          .map((x) => AppointmentModel.fromJson(x))
          .toList();
      filteredAppointments = appointmentDataList;
      _isVisible = true;
    });
  }

  String _formatDateTime(DateTime dateTime) {
    final formatter = DateFormat('dd/MM/yyyy HH:mm');
    return formatter.format(dateTime);
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
    return colorList[random.nextInt(colorList.length)];
  }

  void _filterAppointments(String query) {
    if (query.isEmpty) {
      setState(() {
        filteredAppointments = appointmentDataList;
      });
    } else {
      setState(() {
        filteredAppointments = appointmentDataList!
            .where((appointment) =>
        appointment.patient!.nom!.toLowerCase().contains(query.toLowerCase()) ||
            appointment.patient!.prenom!.toLowerCase().contains(query.toLowerCase()) ||
            appointment.medecin!.nom!.toLowerCase().contains(query.toLowerCase()) ||
            appointment.medecin!.prenom!.toLowerCase().contains(query.toLowerCase()))
            .toList();
      });
    }
  }


  @override
  Widget build(BuildContext context) {
    Size size = MediaQuery.of(context).size;

    return SingleChildScrollView(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          HeaderWithSearchBox(size: size, onSearchChanged: _filterAppointments),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: <Widget>[
              Text("Requests", style: TextStyles.title.bold),
              IconButton(
                icon: Icon(
                  Icons.sort,
                  color: Theme.of(context).primaryColor,
                ),
                onPressed: () {},
              ),
            ],
          ).hP16,
          SizedBox(height: 20),
          getAppointmentsWidgetList(),
        ],
      ),
    );
  }

  Widget getAppointmentsWidgetList() {
    return Visibility(
      visible: _isVisible,
      child: (filteredAppointments != null)
          ? ListView.builder(
        shrinkWrap: true,
        physics: NeverScrollableScrollPhysics(),
        itemCount: filteredAppointments!.length,
        itemBuilder: (context, index) {
          return _appointmentTile(filteredAppointments![index]);
        },
      )
          : Column(
        children: [
          Text('Data not loaded yet'),
          Text('Data not loaded yet'),
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
                "Dr: ${model.medecin!.nom} ${model.medecin!.prenom}",
                style: TextStyles.title.bold,
              ),
              Text(
                "Patient: ${model.patient!.nom} ${model.patient!.prenom}",
                style: TextStyles.bodySm.subTitleColor.bold,
              ),
              Text(
                "Date: ${_formatDateTime(model.date!)}",
                style: TextStyles.bodySm.subTitleColor.bold,
              ),
              Text(
                "Status: ${model.status!}",
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
          Navigator.of(context).push(
            MaterialPageRoute(
              builder: (_) => AppointmentDetailsPage(model: model),
            ),
          );
        },
        borderRadius: BorderRadius.all(Radius.circular(20)),
      ),
    );
  }
}
