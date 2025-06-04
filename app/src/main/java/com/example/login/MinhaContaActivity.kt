package com.example.login

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MinhaContaActivity : AppCompatActivity() {

    private lateinit var profileImage: CircleImageView
    private lateinit var tvChangePhoto: TextView
    private lateinit var editTextNome: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextCPF: EditText
    private lateinit var editTextTelefone: EditText
    private lateinit var fabSaveProfile: FloatingActionButton

    private lateinit var apiService: ApiService
    private lateinit var preferencesManager: PreferencesManager
    private var currentUser: Usuario? = null
    private var selectedImageUri: Uri? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(
                this,
                getString(R.string.permission_required_gallery),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let {
                selectedImageUri = it
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.ic_default_profile)
                    .error(R.drawable.ic_default_profile)
                    .into(profileImage)
            } ?: run {
                Toast.makeText(
                    this,
                    getString(R.string.image_selection_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                this,
                getString(R.string.image_loading_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minha_conta)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_minha_conta)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.minha_conta)

        preferencesManager = PreferencesManager(this)

        profileImage = findViewById(R.id.profile_image)
        tvChangePhoto = findViewById(R.id.tv_change_photo)
        editTextNome = findViewById(R.id.editTextNome)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextCPF = findViewById(R.id.editTextCPF)
        editTextTelefone = findViewById(R.id.editTextTelefone)
        fabSaveProfile = findViewById(R.id.fab_save_profile)

        editTextCPF.isEnabled = false
        editTextTelefone.isEnabled = true

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.15.128/meu_projeto_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        loadProfileData()

        tvChangePhoto.setOnClickListener {
            checkGalleryPermissionAndOpen()
        }

        fabSaveProfile.setOnClickListener {
            saveProfileData()
        }
    }

    private fun loadProfileData() {
        currentUser = preferencesManager.getUser()

        currentUser?.let {
            editTextNome.setText(it.usuarioNome)
            editTextEmail.setText(it.usuarioEmail)
            editTextCPF.setText(it.usuarioCpf)
            editTextTelefone.setText(it.usuarioTelefone ?: "")

            if (!it.usuarioImagemUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(it.usuarioImagemUrl)
                    .placeholder(R.drawable.ic_default_profile)
                    .error(R.drawable.ic_default_profile)
                    .into(profileImage)
            } else {
                profileImage.setImageResource(R.drawable.ic_default_profile)
            }
        } ?: run {
            Toast.makeText(this, "Erro: Usuário não carregado do SharedPreferences. Faça login novamente.", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkGalleryPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE // Esta é a linha 174 no seu código, a ser corrigida
            ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES) -> {
                Toast.makeText(
                    this,
                    "Precisamos de acesso à sua galeria para selecionar a foto de perfil.",
                    Toast.LENGTH_LONG
                ).show()
                requestPermissionLauncher.launch(
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE // Corrigido aqui
                    }
                )
            }
            else -> {
                requestPermissionLauncher.launch(
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE // Corrigido aqui
                    }
                )
            }
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(galleryIntent)
    }

    private fun saveProfileData() {
        val nomeAtualizado = editTextNome.text.toString().trim()
        val emailAtualizado = editTextEmail.text.toString().trim()
        val telefoneAtualizado = editTextTelefone.text.toString().trim()

        if (nomeAtualizado.isEmpty() || emailAtualizado.isEmpty() || telefoneAtualizado.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        val usuarioId = currentUser?.usuarioId
        if (usuarioId == null || usuarioId == 0) {
            Toast.makeText(this, "Erro: ID do usuário não encontrado para atualização. Faça login novamente.", Toast.LENGTH_LONG).show()
            return
        }

        val imagemUrlParaSalvar: String? = selectedImageUri?.toString() ?: currentUser?.usuarioImagemUrl

        apiService.editarUsuario(
            usuarioId = usuarioId,
            usuarioNome = nomeAtualizado,
            usuarioEmail = emailAtualizado,
            usuarioTelefone = telefoneAtualizado,
            usuarioImagemUrl = imagemUrlParaSalvar
        ).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@MinhaContaActivity,
                        getString(R.string.profile_updated_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    currentUser = currentUser?.copy(
                        usuarioNome = nomeAtualizado,
                        usuarioEmail = emailAtualizado,
                        usuarioTelefone = telefoneAtualizado,
                        usuarioImagemUrl = imagemUrlParaSalvar
                    )
                    currentUser?.let {
                        preferencesManager.saveUser(it)
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(
                        this@MinhaContaActivity,
                        "Erro ao atualizar perfil: ${response.code()} - $errorBody",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(
                    this@MinhaContaActivity,
                    "Falha na conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}