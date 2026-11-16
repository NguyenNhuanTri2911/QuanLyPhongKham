package vn.udn.ute.appck.apphealthmate.ui.doctor

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import vn.udn.ute.appck.apphealthmate.R
import vn.udn.ute.appck.apphealthmate.databinding.ActivityDoctorExamineBinding
import vn.udn.ute.appck.apphealthmate.databinding.ItemAdminCardBinding
import vn.udn.ute.appck.apphealthmate.model.LichHen
import vn.udn.ute.appck.apphealthmate.repository.AuthRepository
import vn.udn.ute.appck.apphealthmate.repository.DoctorRepository
import vn.udn.ute.appck.apphealthmate.repository.PatientRepository

/**
 * Màn hình Khám bệnh chi tiết & Xem bệnh sử của Bệnh nhân.
 */
class DoctorExamineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorExamineBinding
    private var appointmentId: Int = 1
    private var currentAppointment: LichHen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorExamineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appointmentId = intent.getIntExtra("EXTRA_APPOINTMENT_ID", 1)
        binding.btnBack.setOnClickListener { finish() }

        binding.btnMarkCompleted.setOnClickListener {
            DoctorRepository.updateAppointmentStatus(appointmentId, "Đã khám")
            Toast.makeText(this, "Đã cập nhật trạng thái: Đã khám!", Toast.LENGTH_SHORT).show()
            loadExamineDetails()
        }

        binding.btnMarkAbsent.setOnClickListener {
            DoctorRepository.updateAppointmentStatus(appointmentId, "Đã vắng")
            Toast.makeText(this, "Đã đánh dấu: Bệnh nhân vắng mặt!", Toast.LENGTH_SHORT).show()
            loadExamineDetails()
        }

        binding.btnOpenPrescription.setOnClickListener {
            val ap = currentAppointment ?: return@setOnClickListener
            val intent = Intent(this, DoctorPrescriptionActivity::class.java)
            intent.putExtra("EXTRA_APPOINTMENT_ID", ap.maLichHen)
            intent.putExtra("EXTRA_PATIENT_PHONE", ap.soDienThoaiBn)
            intent.putExtra("EXTRA_PATIENT_NAME", ap.tenNguoiKham ?: ap.tenBenhNhan)
            intent.putExtra("EXTRA_SYMPTOMS", ap.trieuChung)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadExamineDetails()
    }

    private fun loadExamineDetails() {
        val ap = DoctorRepository.getAppointmentById(appointmentId) ?: return
        currentAppointment = ap

        val patientUser = AuthRepository.getUserByPhone(ap.soDienThoaiBn)
        binding.tvPatientName.text = "BN: ${ap.tenNguoiKham ?: ap.tenBenhNhan} (${ap.trangThai})"

        if (patientUser != null && patientUser.anThongTin) {
            binding.tvPatientVitals.text = "Bệnh nhân đang bật chế độ Ẩn thông tin cá nhân nhạy cảm"
        } else {
            binding.tvPatientVitals.text = "SĐT: ${ap.soDienThoaiBn} • Nhóm máu: ${patientUser?.nhomMau ?: "O+"} • Cao: ${patientUser?.chieuCao ?: "170"}cm • Nặng: ${patientUser?.canNang ?: "65"}kg"
        }

        binding.tvAppointmentSymptoms.text = "Ngày khám: ${ap.ngayKham} (${ap.khungGio})\nTriệu chứng: ${ap.trieuChung}"

        binding.layoutHistoryPrescriptions.removeAllViews()
        val history = PatientRepository.getPrescriptionsByPatient(ap.soDienThoaiBn)
        if (history.isEmpty()) {
            val tvEmpty = TextView(this).apply {
                text = "Bệnh nhân chưa có đơn thuốc cũ."
                setTextColor(Color.parseColor("#64748B"))
                textSize = 13f
            }
            binding.layoutHistoryPrescriptions.addView(tvEmpty)
            return
        }

        for (dt in history) {
            val rxCard = ItemAdminCardBinding.inflate(layoutInflater, binding.layoutHistoryPrescriptions, false)
            rxCard.boxAvatar.setBackgroundResource(R.drawable.bg_squircle_blue)
            rxCard.tvAvatarText.text = "Rx"
            rxCard.tvItemTitle.text = dt.chanDoan
            rxCard.tvItemSubtitle.text = "Đơn #${dt.maDonThuoc} (${dt.ngayKe}) • BS: ${dt.tenBacSi}"
            rxCard.tvItemBadge.text = "${dt.danhSachThuoc.size} thuốc"
            val meds = dt.danhSachThuoc.joinToString("\n") { "• ${it.tenThuoc} (${it.soLuong} ${it.loaiThuoc}) - ${it.lieuLuong}" }
            rxCard.tvItemDetails.text = meds
            binding.layoutHistoryPrescriptions.addView(rxCard.root)
        }
    }
}
