package com.example.passwordgenerator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import java.io.IOException
import java.security.SecureRandom

class MainActivity : AppCompatActivity() {
    private lateinit var textViewPassword: TextView
    private lateinit var textViewStrength: TextView
    private lateinit var textViewLengthLabel: TextView
    private lateinit var seekBarLength: SeekBar
    private lateinit var checkBoxUpper: CheckBox
    private lateinit var checkBoxLower: CheckBox
    private lateinit var checkBoxNumbers: CheckBox
    private lateinit var checkBoxSymbols: CheckBox
    private lateinit var buttonCopy: ImageButton
    private lateinit var buttonRegenerate: ImageButton
    private lateinit var buttonSaveFile: MaterialButton

    private val secureRandom = SecureRandom()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        textViewPassword = findViewById(R.id.textViewPassword)
        textViewStrength = findViewById(R.id.textViewStrength)
        textViewLengthLabel = findViewById(R.id.textViewLengthLabel)
        seekBarLength = findViewById(R.id.seekBarLength)
        checkBoxUpper = findViewById(R.id.checkBoxUpper)
        checkBoxLower = findViewById(R.id.checkBoxLower)
        checkBoxNumbers = findViewById(R.id.checkBoxNumbers)
        checkBoxSymbols = findViewById(R.id.checkBoxSymbols)
        buttonCopy = findViewById(R.id.buttonCopy)
        buttonRegenerate = findViewById(R.id.buttonRegenerate)
        buttonSaveFile = findViewById(R.id.buttonSaveFile)

        seekBarLength.max = 127
        seekBarLength.progress = 11 // Default to 12

        setupListeners()
        updateLengthText(12)
        generateAndDisplayPassword()
    }

    private fun setupListeners() {
        seekBarLength.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val length = progress + 1
                updateLengthText(length)
                generateAndDisplayPassword()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        buttonRegenerate.setOnClickListener { generateAndDisplayPassword() }

        buttonCopy.setOnClickListener {
            val password = textViewPassword.text.toString()
            if (password.isNotEmpty() && password != getString(R.string.select_option)) {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(getString(R.string.generated_password_label), password)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
            }
        }

        buttonSaveFile.setOnClickListener {
            val password = textViewPassword.text.toString()
            if (password.isNotEmpty() && password != getString(R.string.select_option)) {
                showSaveDialog()
            }
        }

        checkBoxUpper.setOnCheckedChangeListener { _, _ -> generateAndDisplayPassword() }
        checkBoxLower.setOnCheckedChangeListener { _, _ -> generateAndDisplayPassword() }
        checkBoxNumbers.setOnCheckedChangeListener { _, _ -> generateAndDisplayPassword() }
        checkBoxSymbols.setOnCheckedChangeListener { _, _ -> generateAndDisplayPassword() }
    }

    private fun showSaveDialog() {
        val input = EditText(this)
        input.hint = getString(R.string.file_name_hint)
        input.setSingleLine()

        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val margin = (16 * resources.displayMetrics.density).toInt()
        params.setMargins(margin, margin / 2, margin, 0)
        input.layoutParams = params
        container.addView(input)

        AlertDialog.Builder(this)
            .setTitle(R.string.save_dialog_title)
            .setMessage(R.string.save_dialog_message)
            .setView(container)
            .setPositiveButton(R.string.save) { _, _ ->
                val fileName = input.text.toString().trim()
                if (fileName.isNotEmpty()) {
                    savePasswordToFile(fileName)
                } else {
                    Toast.makeText(this, R.string.enter_file_name, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun savePasswordToFile(fileName: String) {
        val password = textViewPassword.text.toString()
        val resolver = contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.txt")
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI

        val uri = resolver.insert(collection, contentValues)

        if (uri != null) {
            try {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(password.toByteArray())
                }
                Toast.makeText(this, R.string.file_saved, Toast.LENGTH_SHORT).show()
            } catch (_: IOException) {
                Toast.makeText(this, R.string.error_saving_file, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, R.string.error_saving_file, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateLengthText(length: Int) {
        textViewLengthLabel.text = getString(R.string.password_length_label, length)
    }

    private fun generateAndDisplayPassword() {
        val length = seekBarLength.progress + 1
        val charPool = StringBuilder()

        if (checkBoxUpper.isChecked) charPool.append(UPPER)
        if (checkBoxLower.isChecked) charPool.append(LOWER)
        if (checkBoxNumbers.isChecked) charPool.append(NUMBERS)
        if (checkBoxSymbols.isChecked) charPool.append(SYMBOLS)

        if (charPool.isEmpty()) {
            textViewPassword.text = getString(R.string.select_option)
            textViewStrength.text = getString(R.string.strength_label, getString(R.string.strength_na))
            textViewStrength.setTextColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.darker_gray
                )
            )
            return
        }

        val password = StringBuilder()
        repeat(length) {
            val index = secureRandom.nextInt(charPool.length)
            password.append(charPool[index])
        }

        textViewPassword.text = password.toString()
        updateStrength(length)
    }

    private fun updateStrength(length: Int) {
        var score = 0

        if (length >= 8) score += 1
        if (length >= 12) score += 1
        if (length >= 20) score += 1

        var types = 0
        if (checkBoxUpper.isChecked) types++
        if (checkBoxLower.isChecked) types++
        if (checkBoxNumbers.isChecked) types++
        if (checkBoxSymbols.isChecked) types++

        score += types

        val strengthString: String
        @AttrRes val colorAttr: Int

        when {
            length < 6 || score <= 3 -> {
                strengthString = getString(R.string.strength_weak)
                colorAttr = R.attr.colorStrengthWeak
            }
            score <= 5 -> {
                strengthString = getString(R.string.strength_medium)
                colorAttr = R.attr.colorStrengthMedium
            }
            score == 6 -> {
                strengthString = getString(R.string.strength_strong)
                colorAttr = R.attr.colorStrengthStrong
            }
            else -> {
                strengthString = getString(R.string.strength_very_strong)
                colorAttr = R.attr.colorStrengthVeryStrong
            }
        }

        textViewStrength.text = getString(R.string.strength_label, strengthString)

        // Resolve attribute color
        val typedValue = TypedValue()
        theme.resolveAttribute(colorAttr, typedValue, true)
        textViewStrength.setTextColor(typedValue.data)
    }

    companion object {
        private const val UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val LOWER = "abcdefghijklmnopqrstuvwxyz"
        private const val NUMBERS = "0123456789"
        private const val SYMBOLS = "!@#$%^&*()-_=+[]{}|;:,.<>?"
    }
}
