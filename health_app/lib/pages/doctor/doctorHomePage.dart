import 'dart:async';
import 'dart:convert';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:health_app/models/doctor_model.dart';
import 'package:health_app/pages/doctor/components/bodyDoctor.dart';
import 'package:health_app/pages/doctor/components/menuDoctorWidget.dart';
import 'package:health_app/pages/doctor/drawerDoctorScreen.dart';
import 'package:health_app/pages/login_page.dart';
import 'package:health_app/services/api_service.dart';

class DoctorHomePage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => DoctorHomePageState();
}

class DoctorHomePageState extends State<DoctorHomePage> {
  Image? image;
  bool? _loadingInProgress;
  ApiService apiService=new ApiService();
  static var userData;
  static String? ImageOfUser;
  static var idDoctor;

  @override
  void initState() {
    super.initState();
     WidgetsBinding.instance!.addPostFrameCallback((_) async {
      getActualUser().then((userData) {
        var doctor = DoctorModel.fromJson(userData['medecin']);
        setState(() {
          DrawerDoctorScreenState.idDoctor = doctor.id;
        });
        DrawerDoctorScreenState.idDoctor = doctor.id;
        //storeUser();

        setState(() {
          LoginPageState.username;
          LoginPageState.firstName=doctor.nom;
          LoginPageState.lastName=doctor.prenom;

          DoctorHomePageState.idDoctor=doctor.id;
          print("ttttttt");
          print(doctor.id);
          print(DoctorHomePageState.idDoctor);
        });
      });
    });
  }
/*     */

  Future getActualUser() async {
    var res = await apiService.getMyProfile();
    userData = json.decode(res.body);

    return userData;
  }

  void displayImage() async {
    print("entred to displayImage");
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: NestedScrollView(
        headerSliverBuilder: ((context, innerBoxIsScrolled) => [
              SliverAppBar(
                  actions: [
                    Container(
                      margin: EdgeInsets.fromLTRB(0, 7, 7, 0),
                      child: FloatingActionButton.small(
                        onPressed: () {},
                        child: FutureBuilder(
                          future: apiService.getDoctorOfUser(),
                          builder:
                              (BuildContext context, AsyncSnapshot snapshot) {
                            if (snapshot.connectionState !=
                                ConnectionState.done) {
                              return CircularProgressIndicator();// your widget while loading
                            }

                            if (!snapshot.hasData) {
                              return Container(); //your widget when error happens
                            }

                            var res = snapshot.data; //your Map<String,dynamic>
                            var data=json.decode(res.body);

                            return ClipOval(
                                child: Image.memory(
                              base64Decode(
                                  data['photo'].toString()),
                            )
                            ); //place your widget here
                          },
                        ),
                        /*  ClipOval(
                      child: Image.memory(
                    base64Decode(DrawerPatientScreenState.ImageOfUser!),
                  )
                  ), */
                      ),
                    ),
                  ],
                  backgroundColor: Color(0xFF0857de),
                  elevation: 0,
                  leading: MenuDoctorWidget()),
            ]),
        body: BodyDoctor(),
      ),
      //body: Body(),
    );
  }
}
