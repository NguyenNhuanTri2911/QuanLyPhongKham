package vn.udn.ute.appck.apphealthmate.ui.doctor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import vn.udn.ute.appck.apphealthmate.databinding.ActivityDoctorProfileBinding
import vn.udn.ute.appck.apphealthmate.repository.DoctorRepository
import vn.udn.ute.appck.apphealthmate.repository.PatientRepository
import vn.udn.ute.appck.apphealthmate.ui.auth.LoginActivity
import vn.udn.ute.appck.apphealthmate.util.SessionManager

/**
 * Màn hình Hồ sơ chuyên môn Bác sĩ (Cập nhật bảng dbo.BacSi).
 */
class DoctorProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        val docId = SessionManager.currentDoctorId ?: 1
        val doc = PatientRepository.getDoctorById(docId)
        doc?.let {
            binding.etDoctorName.setText(it.hoTen)
            binding.etDegree.setText(it.hocVi)
            binding.etExperience.setText(it.kinhNghiem)
            binding.etPhone.setText(it.soDienThoai)
            binding.etBio.setText(it.chiTietChuyenMon)
        }

        binding.btnSaveDoctorProfile.setOnClickListener {
            val name = binding.etDoctorName.text?.toString().orEmpty().trim()
            val degree = binding.etDegree.text?.toString().orEmpty().trim()
            val exp = binding.etExperience.text?.toString().orEmpty().trim()
            val phone = binding.etPhone.text?.toString().orEmpty().trim()
            val bio = binding.etBio.text?.toString().orEmpty().trim()

            DoctorRepository.updateDoctorProfile(docId, name, degree, exp, phone, bio)
            SessionManager.currentName = name
            Toast.makeText(this, "Đã lưu hồ sơ chuyên môn lên SQL Server!", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogoutDoctor.setOnClickListener {
            SessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}

