package vn.udn.ute.appck.apphealthmate.ui.admin

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import vn.udn.ute.appck.apphealthmate.R
import vn.udn.ute.appck.apphealthmate.databinding.ActivityAdminDoctorsBinding
import vn.udn.ute.appck.apphealthmate.databinding.ItemAdminCardBinding
import vn.udn.ute.appck.apphealthmate.repository.AdminRepository
import vn.udn.ute.appck.apphealthmate.repository.PatientRepository

/**
 * Màn hình Quản lý Bác sĩ (Thêm bác sĩ mới, Tạm nghỉ / Mở lại hoạt động + tự động hủy lịch hẹn liên quan).
 */
class AdminDoctorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDoctorsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDoctorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnAddDoctor.setOnClickListener { showAddDoctorDialog() }

        loadDoctors()
    }

    private fun loadDoctors() {
        binding.layoutAdminDoctors.removeAllViews()
        val doctors = PatientRepository.getAllDoctors()
        for (doc in doctors) {
            val itemCard = ItemAdminCardBinding.inflate(layoutInflater, binding.layoutAdminDoctors, false)
            val initials = doc.hoTen.trim().split(" ").takeLast(2).joinToString("") { it.take(1).uppercase() }
            itemCard.boxAvatar.setBackgroundResource(
                if (doc.trangThaiHoatDong) R.drawable.bg_squircle_blue else R.drawable.bg_squircle_red
            )
            itemCard.tvAvatarText.text = initials.ifEmpty { "BS" }
            itemCard.tvItemTitle.text = "${doc.hocVi} ${doc.hoTen}"
            itemCard.tvItemSubtitle.text = "Khoa ${doc.tenKhoa} • SĐT: ${doc.soDienThoai.ifBlank { "N/A" }}"
            itemCard.tvItemBadge.text = if (doc.trangThaiHoatDong) "Đang làm việc" else "Đang tạm nghỉ"
            itemCard.tvItemBadge.setBackgroundResource(
                if (doc.trangThaiHoatDong) R.drawable.bg_badge_green else R.drawable.bg_badge_red
            )
            itemCard.tvItemBadge.setTextColor(
                Color.parseColor(if (doc.trangThaiHoatDong) "#059669" else "#DC2626")
            )
            itemCard.tvItemDetails.text = "Kinh nghiệm: ${doc.kinhNghiem}\nChuyên môn: ${doc.chiTietChuyenMon}"
            itemCard.layoutActions.visibility = View.VISIBLE

            if (doc.trangThaiHoatDong) {
                itemCard.btnPrimary.text = "Chuyển sang Tạm nghỉ (Tự động hủy lịch hẹn)"
                itemCard.btnPrimary.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FEF2F2"))
                itemCard.btnPrimary.setTextColor(Color.parseColor("#DC2626"))
            } else {
                itemCard.btnPrimary.text = "Mở lại trạng thái làm việc"
                itemCard.btnPrimary.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ECFDF5"))
                itemCard.btnPrimary.setTextColor(Color.parseColor("#059669"))
            }

            itemCard.btnPrimary.setOnClickListener {
                val newStatus = !doc.trangThaiHoatDong
                AdminRepository.toggleDoctorActive(doc, newStatus)
                Toast.makeText(
                    this,
                    if (newStatus) "Đã kích hoạt lại bác sĩ!" else "Đã tạm ngưng bác sĩ & hủy lịch hẹn liên quan!",
                    Toast.LENGTH_SHORT
                ).show()
                loadDoctors()
            }

            binding.layoutAdminDoctors.addView(itemCard.root)
        }
    }

    private fun showAddDoctorDialog() {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 8)
        }
        val etName = EditText(this).apply { hint = "Họ và tên Bác sĩ" }
        val etDegree = EditText(this).apply { hint = "Học vị (ThS.BS, TS.BS, PGS.TS)"; setText("ThS.BS") }
        val etPhone = EditText(this).apply { hint = "SĐT đăng nhập (Mật khẩu mặc định: 123456)" }
        val etBio = EditText(this).apply { hint = "Chuyên môn chi tiết" }
        container.addView(etName)
        container.addView(etDegree)
        container.addView(etPhone)
        container.addView(etBio)

        MaterialAlertDialogBuilder(this)
            .setTitle("Thêm Bác sĩ mới (dbo.BacSi)")
            .setView(container)
            .setPositiveButton("Lưu") { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isEmpty()) return@setPositiveButton
                AdminRepository.addDoctor(
                    hoTen = name,
                    maKhoa = 1,
                    hocVi = etDegree.text.toString().trim().ifEmpty { "BS" },
                    kinhNghiem = "8 năm kinh nghiệm",
                    sdt = etPhone.text.toString().trim(),
                    chuyenMon = etBio.text.toString().trim().ifEmpty { "Khám và điều trị chuyên khoa" }
                )
                Toast.makeText(this, "Đã thêm bác sĩ mới!", Toast.LENGTH_SHORT).show()
                loadDoctors()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
