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
import vn.udn.ute.appck.apphealthmate.databinding.ActivityAdminDepartmentsBinding
import vn.udn.ute.appck.apphealthmate.databinding.ItemAdminCardBinding
import vn.udn.ute.appck.apphealthmate.repository.AdminRepository
import vn.udn.ute.appck.apphealthmate.repository.PatientRepository

/**
 * Màn hình Quản lý Chuyên khoa (Thêm / Sửa / Xóa trên bảng dbo.ChuyenKhoa).
 */
class AdminDepartmentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDepartmentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDepartmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnAddDepartment.setOnClickListener { showDepartmentDialog() }

        loadDepartments()
    }

    private fun loadDepartments() {
        binding.layoutAdminDepartments.removeAllViews()
        val depts = PatientRepository.getAllDepartments()
        for (dept in depts) {
            val itemCard = ItemAdminCardBinding.inflate(layoutInflater, binding.layoutAdminDepartments, false)
            itemCard.boxAvatar.setBackgroundResource(R.drawable.bg_squircle_blue)
            itemCard.tvAvatarText.text = dept.tenKhoa.take(2).uppercase()
            itemCard.tvItemTitle.text = "Khoa ${dept.tenKhoa}"
            itemCard.tvItemSubtitle.text = "Mã chuyên khoa #${dept.maKhoa}"
            itemCard.tvItemBadge.text = "Chuyên khoa"
            itemCard.tvItemDetails.text = dept.moTa ?: "Khám và điều trị chuyên sâu"
            itemCard.layoutActions.visibility = View.VISIBLE
            itemCard.btnPrimary.text = "Xóa chuyên khoa này"
            itemCard.btnPrimary.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FEF2F2"))
            itemCard.btnPrimary.setTextColor(Color.parseColor("#DC2626"))
            itemCard.btnPrimary.setOnClickListener {
                AdminRepository.deleteDepartment(dept.maKhoa)
                Toast.makeText(this, "Đã xóa chuyên khoa!", Toast.LENGTH_SHORT).show()
                loadDepartments()
            }
            binding.layoutAdminDepartments.addView(itemCard.root)
        }
    }

    private fun showDepartmentDialog() {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 8)
        }
        val etName = EditText(this).apply { hint = "Tên chuyên khoa mới" }
        val etDesc = EditText(this).apply { hint = "Mô tả chức năng chuyên khoa" }
        container.addView(etName)
        container.addView(etDesc)

        MaterialAlertDialogBuilder(this)
            .setTitle("Thêm Chuyên khoa mới")
            .setView(container)
            .setPositiveButton("Thêm") { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isEmpty()) return@setPositiveButton
                AdminRepository.addDepartment(name, etDesc.text.toString().trim())
                Toast.makeText(this, "Đã thêm chuyên khoa $name!", Toast.LENGTH_SHORT).show()
                loadDepartments()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
