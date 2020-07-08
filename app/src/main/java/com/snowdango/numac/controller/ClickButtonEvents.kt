package com.snowdango.numac.controller

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import com.snowdango.numac.activites.NumAcActivity

/*
This class listen by AppDataEditorFragment.
can uninstall app and replace command old command to new command.
When uninstall app , this app is delete by app list.
can check new command before replace command throw Error by Alert.
 */

class ClickButtonEvents {
    fun uninstallApp(oldCommand: String?): Intent? {
        var intent: Intent? = null
        try {
            val info = NumAcActivity.dataBaseHelper!!.getPackageAndClass(NumAcActivity.dataBaseHelper!!, oldCommand!!)
            intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE, Uri.fromParts("package", info[0], null))
        } catch (e: Exception) {
            val clickButtonEvents = ClickButtonEvents()
            clickButtonEvents.alertCreate("Error", "can\'t uninstall app",
                    "OK", null, null, null)
        } finally {
            return intent
        }
    }

    fun deleteApp(appId: Int, appPackageName: String?, context: Context?): Boolean { //delete app for list
        var errorChecker = true
        try {
            NumAcActivity.dataBaseHelper!!.deleteApp(NumAcActivity.dataBaseHelper!!, appPackageName!!)
            NumAcActivity.list!!.removeAt(appId)
            val firstLoadAppDb = FirstLoadAppDb()
            firstLoadAppDb.updateList(NumAcActivity.dataBaseHelper!!, context)
        } catch (e: Exception) {
            errorChecker = false
        } finally {
            return errorChecker
        }
    }

    fun checkCommandFormat(newCommand: String): Boolean {
        var matchNumber = true
        if (newCommand.length != 4) {
            matchNumber = false
            alertCreate("Error code3-2", """
     This command doesn't follow the format.
     You should choose 4 numbers.
     """.trimIndent(),
                    "OK", null, null, null)
        }
        for (element in newCommand) {
            if (Character.isDigit(element)) {
                continue
            } else {
                matchNumber = false
                alertCreate("Error 3-3", """
     This command doesn't follow the format.
     You should choose 4 numbers.
     """.trimIndent(),
                        "OK", null, null, null)
                break
            }
        }
        return matchNumber
    }

    fun checkNewCommandForList(newCommand: String): Boolean {
        var checkCommandExist = true
        for (a in NumAcActivity.list!!) {
            if (a.appCommand == newCommand) {
                alertCreate("Error code 3-4", """This command already exist in list.Please choose deference 4 numbers.""".trimIndent(),
                        "OK", null, null, null)
                checkCommandExist = false
                break
            }
        }
        return checkCommandExist
    }

    fun changeAppCommand(appPosition: Int, appPackageName: String?, newCommand: String?): Boolean {
        var errorChecker = true
        try {
            NumAcActivity.list!![appPosition].appCommand = newCommand
            NumAcActivity.dataBaseHelper!!.updateCommandWhereName(NumAcActivity.dataBaseHelper!!, appPackageName!!, newCommand!!)
        } catch (e: Exception) {
            alertCreate("Error code 3-5", """Sorry, I missed change command.Couldn't change this app's command.""".trimIndent(),
                    "OK", null, null, null)
            errorChecker = false
        } finally {
            return errorChecker
        }
    }

    fun alertCreate(title: String?, message: String?, positive: String?, positiveListener: DialogInterface.OnClickListener?,
                    negative: String?, negativeListener: DialogInterface.OnClickListener?) {
        try {
            NumAcActivity.builder!!.setTitle(title)
            NumAcActivity.builder!!.setMessage(message)
            NumAcActivity.builder!!.setPositiveButton(positive, positiveListener)
            NumAcActivity.builder!!.setNegativeButton(negative, negativeListener)
            NumAcActivity.builder!!.show()
        }catch ( e: Exception ){
        }
    }
}