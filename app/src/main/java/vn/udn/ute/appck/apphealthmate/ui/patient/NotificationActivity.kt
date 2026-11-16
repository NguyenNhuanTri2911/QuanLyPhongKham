package vn.udn.ute.appck.apphealthmate.ui.patient

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import vn.udn.ute.appck.apphealthmate.R
import vn.udn.ute.appck.apphealthmate.databinding.ActivityNotificationBinding
import vn.udn.ute.appck.apphealthmate.databinding.ItemAdminCardBinding
import vn.udn.ute.appck.apphealthmate.repository.PatientRepository
import vn.udn.ute.appck.apphealthmate.util.SessionManager

/**
 * Màn hình Thông báo chung (dbo.ThongBao).
 */
class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnMarkAllRead.setOnClickListener {
            val phone = SessionManager.currentPhone.ifBlank { "0912345678" }
            PatientRepository.markAllNotificationsRead(phone)
            Toast.makeText(this, "Đã đánh dấu đọc tất cả!", Toast.LENGTH_SHORT).show()
            loadNotifications()
        }

        loadNotifications()
    }

    private fun loadNotifications() {
        binding.layoutNotifications.removeAllViews()
        val phone = SessionManager.currentPhone.ifBlank { "0912345678" }
        val list = PatientRepository.getNotifications(phone)
        if (list.isEmpty()) {
            val tvEmpty = TextView(this).apply {
                text = "Hiện chưa có thông báo mới."
                setTextColor(Color.parseColor("#64748B"))
                textSize = 14f
            }
            binding.layoutNotifications.addView(tvEmpty)
            return
        }

        for (item in list) {
            val notifCard = ItemAdminCardBinding.inflate(layoutInflater, binding.layoutNotifications, false)
            notifCard.boxAvatar.setBackgroundResource(
                if (item.daDoc) R.drawable.bg_squircle_blue else R.drawable.bg_squircle_amber
            )
            notifCard.tvAvatarText.text = if (item.daDoc) "✓" else "!"
            notifCard.tvItemTitle.text = item.tieuDe
            notifCard.tvItemSubtitle.text = "Thời gian: ${item.ngayTao}"
            notifCard.tvItemBadge.text = if (item.daDoc) "Đã đọc" else "Mới"
            notifCard.tvItemBadge.setBackgroundResource(
                if (item.daDoc) R.drawable.bg_badge_blue else R.drawable.bg_badge_amber
            )
            notifCard.tvItemDetails.text = item.noiDung
            binding.layoutNotifications.addView(notifCard.root)
        }
    }
}
