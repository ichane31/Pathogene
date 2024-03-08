import 'dart:convert';
import 'dart:math';
import 'package:flutter/material.dart';
import 'package:health_app/helpers/constant.dart';
import 'package:health_app/models/doctor_model.dart';
import 'package:health_app/pages/patient/components/header_with_searchBox.dart';
import 'package:health_app/pages/patient/components/recommendation.dart';
import 'package:health_app/pages/patient/doctorDetails.dart';
import 'package:health_app/services/api_service.dart';
import 'package:health_app/theme/extention.dart';
import 'package:health_app/services/search_query.dart';


class Body extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _BodyState();
}

class _BodyState extends State<Body> {
  List<DoctorModel>? doctorDataList;
  Image? image;
  ApiService apiService = new ApiService();
  var Doctorsdata;
  bool _isVisible = false;

  @override
  void initState() {
    super.initState();
    getDoctors().then((Doctorsdata) {
      setState(() {
        doctorDataList = (Doctorsdata as List<dynamic>)
            .map((x) => DoctorModel.fromJson(x))
            .toList();
        _isVisible = true;
      });
    });
  }

  Future getDoctors() async {
    var res = await apiService.getDoctors();
    Doctorsdata = json.decode(res.body);
    return Doctorsdata;
  }

  void delay() async {
    await Future.delayed(const Duration(seconds: 5), () {});
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

  void _filterDoctors(String searchText) {
    SearchQuery.query = searchText;
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    Size size = MediaQuery.of(context).size;

    return SingleChildScrollView(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          HeaderWithSearchBox(size: size, onSearchChanged: _filterDoctors),
          Recomendation(),
          SizedBox(
            height: 10,
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: <Widget>[
              Text("Doctors", style: TextStyles.title.bold),
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
          getdoctorWidgetList(),
        ],
      ),
    );
  }

  Widget getdoctorWidgetList() {
    final filteredDoctorList = (doctorDataList != null)
        ? doctorDataList!.where((doctor) {
      final fullName = "Dr ${doctor.nom} ${doctor.prenom}";
      return fullName
          .toLowerCase()
          .contains(SearchQuery.query.toLowerCase());
    }).toList()
        : [];

    return Visibility(
      visible: _isVisible,
      child: Column(
        children: (filteredDoctorList.isNotEmpty)
            ? filteredDoctorList.map((x) {
          return _doctorTile(x);
        }).toList()
            : [
          Text('No matching doctors found'),
        ],
      ),
    );
  }

  Widget _doctorTile(DoctorModel model) {
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
                base64Decode(model.photo),
                height: 50,
                width: 50,
                fit: BoxFit.contain,
              ),
            ),
          ),
          title: Text("Dr ${model.nom} ${model.prenom}",
              style: TextStyles.title.bold),
          subtitle: Text(
            model.type,
            style: TextStyles.bodySm.subTitleColor.bold,
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
              builder: (_) => DoctorDetailsPage(model: model)));
        },
        borderRadius: BorderRadius.all(Radius.circular(20)),
      ),
    );
  }
}
