package com.tijiebo.covidtracker.ui.model

import androidx.annotation.StringRes
import com.tijiebo.covidtracker.R
import java.lang.Exception

class GeneralServiceException(@StringRes val errorResId: Int) : Exception()