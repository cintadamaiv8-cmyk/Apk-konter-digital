package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.theme.*
import com.example.viewmodel.StoreViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: StoreViewModel, navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var showResetDataDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    
    var snackbarHostState = remember { SnackbarHostState() }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            viewModel.exportData(context, it) { success ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(if (success) "Backup berhasil" else "Backup gagal")
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            viewModel.importData(context, it) { success ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(if (success) "Restore berhasil" else "Restore gagal")
                    if (success) {
                        navController.navigate("dashboard") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan", color = NebulaCyan, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = NebulaCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = VoidBlack
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                SettingsItem(
                    icon = Icons.Filled.Backup,
                    title = "Backup Data",
                    subtitle = "Export seluruh database ke file JSON",
                    onClick = { exportLauncher.launch("shinfox_backup.json") }
                )
                Divider(color = SurfaceDark)
                SettingsItem(
                    icon = Icons.Filled.Restore,
                    title = "Restore Data",
                    subtitle = "Import database dari file JSON",
                    onClick = { importLauncher.launch(arrayOf("application/json")) }
                )
                Divider(color = SurfaceDark)
                SettingsItem(
                    icon = Icons.Filled.DeleteForever,
                    title = "Reset Data Aplikasi",
                    subtitle = "Menghapus seluruh data (transaksi, produk, dll)",
                    onClick = { showResetDataDialog = true },
                    isDestructive = true
                )
                Divider(color = SurfaceDark)
                SettingsItem(
                    icon = Icons.Filled.Info,
                    title = "Tentang Aplikasi",
                    subtitle = "Informasi Shinfox Store Ultra X",
                    onClick = { showAboutDialog = true }
                )
                Divider(color = SurfaceDark)
                SettingsItem(
                    icon = Icons.Filled.ExitToApp,
                    title = "Keluar Aplikasi",
                    subtitle = "Tutup aplikasi sepenuhnya",
                    onClick = { showExitDialog = true }
                )
            }
        }
    }

    if (showResetDataDialog) {
        AlertDialog(
            onDismissRequest = { showResetDataDialog = false },
            title = { Text("Reset Data Aplikasi", color = DangerRed) },
            text = { Text("Yakin ingin menghapus seluruh data aplikasi? Aksi ini tidak dapat dibatalkan.", color = StarlightSilver) },
            confirmButton = {
                TextButton(onClick = {
                    showResetDataDialog = false
                    viewModel.resetAppData {
                        coroutineScope.launch { snackbarHostState.showSnackbar("Semua data berhasil dihapus") }
                    }
                }) { Text("Hapus Semua", color = DangerRed) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDataDialog = false }) { Text("Batal", color = NebulaCyan) }
            },
            containerColor = SurfaceDark
        )
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Keluar dari aplikasi?", color = StarlightSilver) },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    (context as? Activity)?.finishAffinity()
                }) { Text("Keluar", color = DangerRed) }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) { Text("Batal", color = NebulaCyan) }
            },
            containerColor = SurfaceDark
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("TENTANG APLIKASI", color = NebulaCyan, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                    item {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.img_app_logo_1781377515277),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        Text(
                            """
                            Shinfox Store Ultra X adalah aplikasi pencatatan transaksi konter yang dirancang untuk membantu pengelolaan usaha konter secara lebih teratur, cepat, dan efisien. Aplikasi ini memungkinkan pengguna mencatat transaksi pelanggan, mengelola produk, memantau aktivitas harian, serta menyimpan riwayat transaksi secara offline tanpa memerlukan koneksi internet.
                            
                            Dengan antarmuka bertema Cosmic yang modern dan nyaman digunakan, Shinfox Store Ultra X berfokus pada kemudahan penggunaan serta kecepatan akses data. Seluruh informasi tersimpan langsung di perangkat sehingga pengguna tetap dapat mengoperasikan aplikasi kapan saja dan di mana saja tanpa ketergantungan pada layanan online.
                            
                            Aplikasi ini dikembangkan untuk mendukung kebutuhan operasional konter sehari-hari, mulai dari pencatatan transaksi sederhana hingga pengelolaan data yang lebih lengkap dalam satu sistem yang ringan, stabil, dan mudah digunakan.
                            
                            Shinfox Store Ultra X mengutamakan kendali penuh pengguna terhadap data. Tidak diperlukan akun, login, sinkronisasi cloud, maupun koneksi internet untuk menjalankan fungsi utama aplikasi. Seluruh data disimpan secara lokal pada perangkat pengguna dan tetap berada di bawah kendali pengguna sepenuhnya.
                            
                            FITUR UTAMA
                            ✓ Pencatatan Transaksi
                            ✓ Manajemen Digital
                            ✓ Riwayat Transaksi
                            ✓ Keuntungan Digital
                            ✓ Statistik Aktivitas
                            ✓ Sistem Hutang / Piutang
                            ✓ Pencarian Cepat
                            ✓ Backup & Restore Data
                            ✓ Reset Data Aplikasi
                            ✓ Tema Cosmic Dark
                            ✓ 100% Offline
                            
                            SYSTEM INFORMATION
                            Origin : Unknown
                            Developer : Anonim
                            Status : Operational
                            Mode : Offline
                            Database : SQLite
                            Version : 1.0.0
                            
                            SHINFOX STORE ULTRA X
                            Cosmic Store Management System
                            
                            All local data remains under user control.
                            No account required.
                            No cloud required.
                            No internet required.
                            """.trimIndent(),
                            color = StarlightSilver,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text("Tutup", color = CosmicPurple) }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun SettingsItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit, isDestructive: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if (isDestructive) DangerRed else NebulaCyan)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, color = if (isDestructive) DangerRed else StarlightSilver, fontWeight = FontWeight.Bold)
            Text(subtitle, color = StarlightSilver.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
        }
    }
}
