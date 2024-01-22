import 'dart:convert';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:health_app/pages/patient/components/MenuSecretaireWidgets.dart';
import 'package:health_app/pages/secretaire/components/bodySecretaire.dart';
import 'package:health_app/services/api_service.dart';

class SecretaireHomePage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => SecretaireHomePageState();
}

class SecretaireHomePageState extends State<SecretaireHomePage> {
  Image? image;
  bool? _loadingInProgress;
  ApiService apiService= ApiService();
  @override
  void initState() {
    super.initState();
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
                      future: apiService.getSecretaireOfUser(),
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
              leading: MenuSecretaireWidget())
          /* SliverAppBar(
                  actions: [
                    Container(
                      margin: EdgeInsets.fromLTRB(0, 7, 7, 0),
                      child: FloatingActionButton.small(
                        onPressed: () {},
                        child: ClipOval(
                            child: Image.asset(
                          "assets/images/person.jpeg",
                        )),
                      ),
                    ),
                  ],
                  backgroundColor: Color(0xFF0857de),
                  elevation: 0,
                  leading: MenuSecretaireWidget()) */
        ]),
        body: BodySecretaire(),
      ),
      //body: Body(),
    );
  }
}
