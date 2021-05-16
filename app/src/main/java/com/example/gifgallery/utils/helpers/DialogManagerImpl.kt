package com.example.gifgallery.utils.helpers

import android.app.Dialog
import android.text.Layout
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.gifgallery.R
import kotlinx.android.synthetic.main.layout_big_image_gif.*
import kotlinx.android.synthetic.main.layout_big_image_gif.view.*

class DialogManagerImpl(val activity: FragmentActivity) : DialogManager {

    private val showedDialog = LinkedHashSet<Dialog>()

    override fun openOneButtonDialog(
        buttonTextId: Int,
        text: String,
        cancelable: Boolean,
        onClickOk: (() -> Unit)?
    ) {
        openOneButtonDialogInternal(buttonTextId, null, text, cancelable, onClickOk)
    }

    override fun openOneButtonDialog(
        buttonTextId: Int,
        textId: Int,
        cancelable: Boolean,
        onClickOk: (() -> Unit)?
    ) {
        openOneButtonDialog(buttonTextId, activity.getString(textId), cancelable, onClickOk)
    }

    override fun openOneButtonDialog(
        buttonTextId: Int,
        title: String,
        message: String,
        cancelable: Boolean,
        onClickOk: (() -> Unit)?
    ) {
        openOneButtonDialogInternal(buttonTextId, title, message, cancelable, onClickOk)
    }

    private fun openOneButtonDialogInternal(
        buttonTextId: Int,
        title: String?,
        message: String,
        cancelable: Boolean,
        onClickOk: (() -> Unit)?
    ) {
        val dialog = AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(activity.getString(buttonTextId)) { dialog, _ ->
                dialog.dismiss()
                onClickOk?.invoke()
            }
            .setCancelable(cancelable)
            .create()

        putToShowedDialogsList(dialog)
        dialog.show()
    }

    override fun openOneButtonImageDialog(
        buttonTextId: Int,
        imageUrl: String,
        cancelable: Boolean,
        onClickOk: (() -> Unit)?
    ) {
        val dialog = AlertDialog.Builder(activity)
            .setView(R.layout.layout_big_image_gif)
            .setPositiveButton(activity.getString(buttonTextId)) { dialog, _ ->
                dialog.dismiss()
                onClickOk?.invoke()
            }
            .setCancelable(cancelable)
            .create()



        putToShowedDialogsList(dialog)
        dialog.show()
        Glide
            .with(dialog.context).load(imageUrl)
            .fitCenter()
            .into(dialog.imageViewBig)
    }

    override fun dismissAll() {
        for (dialog in showedDialog) dialog.dismiss()
    }

    override fun isDialogShown() : Boolean {
        for (dialog in showedDialog) if (dialog.isShowing) return true
        return false
    }


    private fun putToShowedDialogsList(dialog: Dialog) {
        showedDialog.add(dialog)
    }
}