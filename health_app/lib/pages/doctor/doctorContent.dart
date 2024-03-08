import 'dart:convert';

import 'package:flutter/material.dart';
//import 'package:flutter_zoom_drawer/config.dart';
import 'package:flutter_zoom_drawer/flutter_zoom_drawer.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:health_app/pages/doctor/calendarPage.dart';
import 'package:health_app/pages/doctor/doctorHomePage.dart';
import 'package:health_app/pages/doctor/DoctorShedulePage.dart';
import 'package:health_app/pages/profile_page.dart';
import 'package:health_app/services/api_service.dart';

final drawerController = new ZoomDrawerController();

class DoctorContent extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => DoctorContentState();
}

class DoctorContentState extends State<DoctorContent> {
  // static int selectedMenuItemIndex=0;
  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();
  static int selectedIndex = 0;
  ApiService apiService = new ApiService();
  //static var userData;
  void _onItemTapped(int index) {
    setState(() {
      selectedIndex = index;
    });
  }

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    /* getActualUser().then((userData) {
      print("the actual user is ${userData}");
      storeUser();
      
      
    }); */
  }

 

  List<Widget> _pages = [
    /*  Scaffold(
      body: ZoomDrawer(
        style: DrawerStyle.style1,
        angle: -10,
        borderRadius: 50,
        slideWidth: MediaQuery.of(context).size.width * 0.8,
        drawerShadowsBackgroundColor: Colors.orangeAccent,
        controller: drawerController,
        mainScreen: PatientHomePage(),
        menuScreen: MenuPage(),
        menuBackgroundColor: Color(0xFF0857de),
      ),
    ), */
    DoctorHomePage(),
    DoctorSchedulePage(),
   // AppointmentPage(),
   /*  Scaffold(
      body: DefaultTextStyle(
          style: TextStyle(color: Colors.blue, fontSize: 20),
          child: Text("meeting screen")),
    ), */
    Calendar(),
  /*   Scaffold(
      body: DefaultTextStyle(
          style: TextStyle(color: Colors.blue, fontSize: 20),
          child: Text("add calendar")),
    ), */
    ProfilePage()
  ];

  @override
  Widget build(BuildContext context) {
    return Container(
      child: Scaffold(
          key: _scaffoldKey,
          body: _pages[selectedIndex],
          bottomNavigationBar: SizedBox(
            height: 70,
            child: BottomNavigationBar(
              type: BottomNavigationBarType.fixed,
              backgroundColor: Color(0xFF0857de),
              selectedItemColor: Colors.white,
              unselectedItemColor: Colors.white.withOpacity(.60),
              selectedFontSize: 14,
              unselectedFontSize: 14,
              currentIndex: selectedIndex,
              onTap: _onItemTapped,
              items: [
                BottomNavigationBarItem(
                  label: 'home',
                  icon: FaIcon(FontAwesomeIcons.house),
                ),
                BottomNavigationBarItem(
                  label: 'schedule',
                  icon: FaIcon(FontAwesomeIcons.clock),
                ),
                BottomNavigationBarItem(
                  label: 'calender',
                  icon: FaIcon(FontAwesomeIcons.calendar),
                ),
                BottomNavigationBarItem(
                  label: 'profile',
                  icon: FaIcon(FontAwesomeIcons.user),
                ),
              ],
            ),
          )
          /* bottomNavigationBar: BottomNavigationBar(
          currentIndex: _selectedIndex,
          onTap: _onItemTapped,
          
          items: [
            BottomNavigationBarItem(

              icon: FaIcon(FontAwesomeIcons.houseMedical),
              label: "Home",
              backgroundColor: Colors.blue[800]
               ),
            BottomNavigationBarItem(
              icon: FaIcon(FontAwesomeIcons.user),
              label: "Home",
              backgroundColor: Colors.blue[800]
               ),
          ]), */
          ),
    );
  }
}
