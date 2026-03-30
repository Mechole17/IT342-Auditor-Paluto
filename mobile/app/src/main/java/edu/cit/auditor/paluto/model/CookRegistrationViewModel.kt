package edu.cit.auditor.paluto.model

import androidx.lifecycle.ViewModel

class CookRegistrationViewModel : ViewModel() {
    // Step 1 Data
    var firstName = ""
    var lastName = ""
    var address = ""
    var email = ""
    var password = ""

    // Step 2 Data
    var bio = ""
    var specialties = ""
    var experience = 0
    var hourlyRate = 0.0
}