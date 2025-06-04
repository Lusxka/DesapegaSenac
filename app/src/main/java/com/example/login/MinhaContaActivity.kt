package com.example.login

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log // Importar para usar Log.d e Log.e
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

    // Launcher para solicitar permissões da galeria
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

    // Launcher para escolher imagem da galeria
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

        // Torna o campo CPF não editável
        editTextCPF.isEnabled = false
        // O campo E-MAIL será controlado via XML para a aparência "apagada"
        // Não defina editTextEmail.isEnabled = false aqui, pois o XML já cuida disso para a aparência
        // mas a propriedade `enabled` no XML também torna o campo não editável por padrão.
        // Se você quiser que o telefone seja editável, mantenha como true. Se não, false.
        editTextTelefone.isEnabled = true // Deixando telefone editável

        // Inicializa Retrofit e ApiService
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.15.128/meu_projeto_api/") // VERIFIQUE SE ESTE IP ESTÁ CORRETO
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
        // Carrega os dados do usuário do SharedPreferences
        currentUser = preferencesManager.getUser()

        currentUser?.let {
            editTextNome.setText(it.usuarioNome)
            editTextEmail.setText(it.usuarioEmail)
            editTextCPF.setText(it.usuarioCpf)
            editTextTelefone.setText(it.usuarioTelefone ?: "") // Lida com telefone nulo

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
            // Opcional: Redirecionar para a tela de login se o usuário não for encontrado
            // val intent = Intent(this, LoginActivity::class.java)
            // startActivity(intent)
            // finish()
        }
    }

    private fun checkGalleryPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES // Para Android 13+
            ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE // Para Android 12-
            ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            // Se a permissão foi negada antes e o usuário precisa de explicação
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES) -> {
                Toast.makeText(
                    this,
                    "Precisamos de acesso à sua galeria para selecionar a foto de perfil.",
                    Toast.LENGTH_LONG
                ).show()
                // Solicita a permissão apropriada com base na versão do Android
                requestPermissionLauncher.launch(
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }
                )
            }
            // Solicitar a permissão pela primeira vez
            else -> {
                // Solicita a permissão apropriada com base na versão do Android
                requestPermissionLauncher.launch(
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
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
        val emailAtualizado = editTextEmail.text.toString().trim() // O email é lido, mas não editável pelo usuário
        val telefoneAtualizado = editTextTelefone.text.toString().trim()

        // Adicionar logs para depuração
        Log.d("MinhaContaActivity", "Tentando salvar perfil...")
        Log.d("MinhaContaActivity", "Nome atualizado: '$nomeAtualizado'")
        Log.d("MinhaContaActivity", "Email (não editável): '$emailAtualizado'")
        Log.d("MinhaContaActivity", "Telefone atualizado: '$telefoneAtualizado'")

        if (nomeAtualizado.isEmpty() || emailAtualizado.isEmpty() || telefoneAtualizado.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
            Log.w("MinhaContaActivity", "Campos obrigatórios vazios.")
            return
        }

        val usuarioId = currentUser?.usuarioId
        if (usuarioId == null || usuarioId == 0) { // Verifica se o ID é válido
            Toast.makeText(this, "Erro: ID do usuário não encontrado para atualização. Faça login novamente.", Toast.LENGTH_LONG).show()
            Log.e("MinhaContaActivity", "ID do usuário nulo ou zero, impossível salvar.")
            return
        }

        // Se uma nova imagem foi selecionada, use a URI dela. Caso contrário, use a URL da imagem atual do usuário.
        val imagemUrlParaSalvar: String? = selectedImageUri?.toString() ?: currentUser?.usuarioImagemUrl
        Log.d("MinhaContaActivity", "Imagem URL para salvar: '$imagemUrlParaSalvar'")

        apiService.editarUsuario(
            usuarioId = usuarioId,
            usuarioNome = nomeAtualizado,
            usuarioEmail = emailAtualizado, // Envia o email, que não foi modificado na UI
            usuarioTelefone = telefoneAtualizado,
            usuarioImagemUrl = imagemUrlParaSalvar
        ).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("MinhaContaActivity", "Resposta da API - Código: ${response.code()}")
                Log.d("MinhaContaActivity", "Resposta da API - Mensagem: ${response.message()}")

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@MinhaContaActivity,
                        getString(R.string.profile_updated_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("MinhaContaActivity", "Perfil atualizado com sucesso na API.")

                    // Atualiza o objeto currentUser com os dados mais recentes
                    currentUser = currentUser?.copy(
                        usuarioNome = nomeAtualizado,
                        usuarioEmail = emailAtualizado, // Mantenha o email original/carregado
                        usuarioTelefone = telefoneAtualizado,
                        usuarioImagemUrl = imagemUrlParaSalvar
                    )
                    // Salva o usuário atualizado no SharedPreferences
                    currentUser?.let {
                        preferencesManager.saveUser(it)
                        Log.d("MinhaContaActivity", "Usuário atualizado no SharedPreferences.")
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("MinhaContaActivity", "Erro ao atualizar perfil na API: ${response.code()} - $errorBody")
                    Toast.makeText(
                        this@MinhaContaActivity,
                        "Erro ao atualizar perfil: ${response.code()} - $errorBody",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("MinhaContaActivity", "Falha na conexão ao atualizar perfil: ${t.message}", t)
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