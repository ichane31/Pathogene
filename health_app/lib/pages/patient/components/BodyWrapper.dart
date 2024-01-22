import 'dart:html';
import 'package:health_app/pages/patient/components/body.dart';

import 'package:flutter/cupertino.dart';

class BodyWrapper extends StatefulWidget {
  @override
  _BodyWrapperState createState() => _BodyWrapperState();
}

class _BodyWrapperState extends State<BodyWrapper> {
  @override
  Widget build(BuildContext context) {
    return Body();
  }
}
