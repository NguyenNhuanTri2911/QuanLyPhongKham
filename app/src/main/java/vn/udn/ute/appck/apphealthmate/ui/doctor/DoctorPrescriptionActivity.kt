package vn.udn.ute.appck.apphealthmate.ui.doctor

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import vn.udn.ute.appck.apphealthmate.R
import vn.udn.ute.appck.apphealthmate.databinding.ActivityDoctorPrescriptionBinding
import vn.udn.ute.appck.apphealthmate.databinding.ItemAdminCardBinding
import vn.udn.ute.appck.apphealthmate.model.ChiTietDonThuoc
import vn.udn.ute.appck.apphealthmate.repository.DoctorRepository
import vn.udn.ute.appck.apphealthmate.util.SessionManager

/**
 * Màn hình Kê đơn thuốc động (Thêm/xóa nhiều dòng thuốc và lưu vào dbo.DonThuoc + dbo.ChiTietDonThuoc).
 */
class DoctorPrescriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorPrescriptionBinding
    private val medicineList = mutableListOf<ChiTietDonThuoc>()

    private var appointmentId: Int = 1
    private var patientPhone: String = "0912345678"
    private var patientName: String = "Bệnh nhân"
    private var symptoms: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorPrescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appointmentId = intent.getIntExtra("EXTRA_APPOINTMENT_ID", 1)
        patientPhone = intent.getStringExtra("EXTRA_PATIENT_PHONE") ?: "0912345678"
        patientName = intent.getStringExtra("EXTRA_PATIENT_NAME") ?: "Nguyễn Văn Bệnh Nhân"
        symptoms = intent.getStringExtra("EXTRA_SYMPTOMS").orEmpty()

        binding.tvPatientSummary.text = "Kê đơn cho: $patientName ($patientPhone)\nTriệu chứng: $symptoms"
        binding.btnBack.setOnClickListener { finish() }
        binding.etDiagnosis.doAfterTextChanged { binding.tilDiagnosis.error = null }

        // Khởi tạo sẵn 1 thuốc mẫu để thuận tiện thao tác
        medicineList.add(
            ChiTietDonThuoc(
                tenThuoc = "Paracetamol 500mg",
                lieuLuong = "1 viên/lần",
                cachDung = "Uống sáng - tối sau ăn",
                loaiThuoc = "Viên nén",
                soLuong = 10
            )
        )
        renderMedicineList()

        binding.btnAddMedicineItem.setOnClickListener {
            val name = binding.etMedName.text?.toString().orEmpty().trim()
            val dosage = binding.etMedDosage.text?.toString().orEmpty().trim()
            val qty = binding.etMedQuantity.text?.toString().orEmpty().trim().toIntOrNull() ?: 10
            val instr = binding.etMedInstruction.text?.toString().orEmpty().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên thuốc!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            medicineList.add(
                ChiTietDonThuoc(
                    tenThuoc = name,
                    lieuLuong = dosage.ifEmpty { "1 viên/lần" },
                    cachDung = instr.ifEmpty { "Uống sau ăn" },
                    loaiThuoc = "Viên nén",
                    soLuong = qty
                )
            )
            binding.etMedName.setText("")
            renderMedicineList()
        }

        binding.btnSavePrescription.setOnClickListener {
            savePrescriptionToSql()
        }
    }

    private fun renderMedicineList() {
        binding.layoutAddedMedicines.removeAllViews()
        medicineList.forEachIndexed { index, med ->
            val medCard = ItemAdminCardBinding.inflate(layoutInflater, binding.layoutAddedMedicines, false)
            medCard.boxAvatar.setBackgroundResource(R.drawable.bg_squircle_teal)
            medCard.tvAvatarText.text = "#${index + 1}"
            medCard.tvItemTitle.text = med.tenThuoc
            medCard.tvItemSubtitle.text = "Liều lượng: ${med.lieuLuong}"
            medCard.tvItemBadge.text = "SL: ${med.soLuong}"
            medCard.tvItemDetails.text = "Cách dùng: ${med.cachDung}"
            medCard.layoutActions.visibility = View.VISIBLE
            medCard.btnPrimary.text = "Xóa thuốc này khỏi đơn"
            medCard.btnPrimary.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FEF2F2"))
            medCard.btnPrimary.setTextColor(Color.parseColor("#DC2626"))
            medCard.btnPrimary.setOnClickListener {
                medicineList.removeAt(index)
                renderMedicineList()
            }
            binding.layoutAddedMedicines.addView(medCard.root)
        }
    }

    private fun savePrescriptionToSql() {
        val diagnosis = binding.etDiagnosis.text?.toString().orEmpty().trim()
        val note = binding.etDoctorNote.text?.toString().orEmpty().trim()

        if (diagnosis.length < 3) {
            binding.tilDiagnosis.error = "Vui lòng nhập chẩn đoán bệnh lý"
            binding.etDiagnosis.requestFocus()
            return
        }
        if (medicineList.isEmpty()) {
            Toast.makeText(this, "Vui lòng thêm ít nhất 1 loại thuốc vào đơn!", Toast.LENGTH_SHORT).show()
            return
        }

        val docId = SessionManager.currentDoctorId ?: 1
        DoctorRepository.createPrescription(
            doctorId = docId,
            patientPhone = patientPhone,
            appointmentId = appointmentId,
            symptoms = symptoms,
            diagnosis = diagnosis,
            note = note,
            items = medicineList
        )

        MaterialAlertDialogBuilder(this)
            .setTitle("✅ Đã lưu Đơn thuốc thành công!")
            .setMessage("Đơn thuốc điện tử đã được lưu vào bảng dbo.DonThuoc và dbo.ChiTietDonThuoc trên SQL Server, đồng thời gửi thông báo cho bệnh nhân $patientName.")
            .setPositiveButton("Hoàn tất") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }
}
