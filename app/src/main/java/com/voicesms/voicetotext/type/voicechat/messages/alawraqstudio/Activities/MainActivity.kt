package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat

import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.AdView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.FirebaseApp
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters.ViewPagerAdapter
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Fragments.VoiceRecFragment
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Fragments.VoiceSMSFragment
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Fragments.VoiceSearchFragment
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Fragments.VoiceTalkFragment
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.handleSpannableString
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.MainApplication
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ActivityMainBinding
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.CustomDialogExitAppBinding
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.CustomDialogRateUsBinding
import io.paperdb.Paper
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean


class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var file: File
    var isRecording = false
    lateinit var ratingKey: String
    private var exitOnBackPressed = false
    var nonZeroRating=true

    private lateinit var adView: AdView
    private val initialLayoutComplete = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this);

        adView = AdView(this)
//        binding.adViewContainer.addView(adView)

//        binding.adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
//            if (!initialLayoutComplete.getAndSet(true)) {
//                loadBanner()
//            }
//        }

        binding.mainTitle.text = handleSpannableString(getString(R.string.voiceSms))
        addTabs();

//        binding.tabs.setupWithViewPager(binding.viewPager2);
//        setupTabIcons();

        val headerText: TextView =
            binding.navigationView.getHeaderView(0).findViewById(R.id.menu_header_text)

        headerText.text = handleSpannableString(getString(R.string.voiceSms))

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.menuId.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        val savedFilesTextView =
            binding.navigationView.getHeaderView(0)
                .findViewById<LinearLayout>(R.id.saved_files_layout)

        val rateUs =
            binding.navigationView.getHeaderView(0)
                .findViewById<LinearLayout>(R.id.rate_us_layout)

        val privacyPolicy =
            binding.navigationView.getHeaderView(0)
                .findViewById<LinearLayout>(R.id.privacy_layout)

        val shareApp =
            binding.navigationView.getHeaderView(0)
                .findViewById<LinearLayout>(R.id.shareApp_layout)

        val language =
            binding.navigationView.getHeaderView(0)
                .findViewById<LinearLayout>(R.id.language_layout)

        privacyPolicy.setOnClickListener {
            privacyPolicy()
            binding.drawerLayout.closeDrawer(GravityCompat.START)

        }

        shareApp.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            shareApplication()
        }

        language.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this@MainActivity,LangungeActivity::class.java))
//            Toast.makeText(this@MainActivity, getString(R.string.comming_soon), Toast.LENGTH_SHORT).show()
        }

        rateUs.setOnClickListener {
            showRateUsDialog()
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        savedFilesTextView.setOnClickListener {
            startActivity(Intent(this@MainActivity, SavedFilesActivity::class.java))
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }


        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    setExitOnBackPressed(true)
                } else {
                    setExitOnBackPressed(false)
                }
                if (isRecording && position != 2) {
                    binding.viewPager2.currentItem = 2
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.cantswitchtabs),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {

                    if (binding.viewPager2.currentItem == 0) {
                        showExitDialog()
                    } else {
                        binding.viewPager2.currentItem = 0
                    }
                }
            }

        })
    }

    fun hideCustomToolbar() {
        binding.relativeTool.visibility = View.GONE
    }

    fun showCustomToolbar() {
        binding.relativeTool.visibility = View.VISIBLE
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupTabIcons() {
        binding.tabs.getTabAt(0)!!.setIcon(getDrawable(R.drawable.voice_sms_icon))
        binding.tabs.getTabAt(1)!!.setIcon(getDrawable(R.drawable.voice_talk_icon))
        binding.tabs.getTabAt(2)!!.setIcon(getDrawable(R.drawable.voice_rec_icon))
        binding.tabs.getTabAt(3)!!.setIcon(getDrawable(R.drawable.search_icon))
    }

    private fun addTabs() {
//        val adapter = ViewPagerAdapter(getSupportFragmentManager())
//        adapter.addFrag(VoiceSMSFragment(), getString(R.string.voiceSms))
//        adapter.addFrag(VoiceTalkFragment(), getString(R.string.voiceTalk))
//        adapter.addFrag(VoiceRecFragment(), getString(R.string.voiceRec))
//        adapter.addFrag(VoiceSearchFragment(), getString(R.string.voiceSearch))
//        viewPager.adapter = adapter

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        adapter.addFragment(VoiceSMSFragment(), getString(R.string.voiceSms))
        adapter.addFragment(VoiceTalkFragment(), getString(R.string.voiceTalk))
        adapter.addFragment(VoiceRecFragment(), getString(R.string.voiceRec))
        adapter.addFragment(VoiceSearchFragment(), getString(R.string.voiceSearch))

        binding.viewPager2.adapter = adapter
        binding.viewPager2.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT

        TabLayoutMediator(binding.tabs, binding.viewPager2) { tab, position ->
            // Set text for tabs
            tab.text = adapter.getTitle(position)

            // Set icons for tabs
            when (position) {
                0 -> tab.icon = getDrawable(R.drawable.voice_sms_icon)
                1 -> tab.icon = getDrawable(R.drawable.voice_talk_icon)
                2 -> tab.icon = getDrawable(R.drawable.voice_rec_icon)
                3 -> tab.icon = getDrawable(R.drawable.search_icon)
                // Add more cases if you have additional tabs
            }
        }.attach()
    }

    private fun setExitOnBackPressed(exit: Boolean) {
        exitOnBackPressed = exit
    }

    fun setRecordingStatus(isRecording: Boolean) {
        this.isRecording = isRecording
        binding.tabs.isEnabled = !isRecording
    }

    private fun shareApplication() {
        val appPackageName = packageName
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Check out this amazing app: https://play.google.com/store/apps/details?id=$appPackageName"
        )
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, "Share via"))
    }

    private fun showExitDialog() {
        val dialog_binding = CustomDialogExitAppBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialog_binding.root)

        val window: Window = dialog.window!!
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setGravity(Gravity.CENTER)

        dialog.show()

        dialog_binding.cardNo.setOnClickListener {
            dialog.dismiss()
            hideNavBar()
        }

        dialog_binding.cardYes.setOnClickListener {
            dialog.dismiss()
            finishAffinity()
        }

        dialog_binding.closeDialog.setOnClickListener {
            dialog.dismiss()
            hideNavBar()
        }

    }

    private fun showRateUsDialog() {
        ratingKey = getString(R.string.mypref)
        val dialog_binding = CustomDialogRateUsBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialog_binding.root)

        val window: Window = dialog.window!!
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setGravity(Gravity.CENTER)

        dialog_binding.linearRated.visibility = View.VISIBLE
        dialog_binding.linearNotRated.visibility = View.GONE

        dialog.show()

        val ratingExisted = Paper.book().read(ratingKey, 0.0f)
        if (ratingExisted!!.equals(0.0f)) {
            dialog_binding.ratingBar.setIsIndicator(false)
            dialog_binding.linearRated.visibility = View.GONE
            dialog_binding.linearNotRated.visibility = View.VISIBLE
        } else {
            dialog_binding.rateappTxt.text = getString(R.string.thanksforSharing)
            dialog_binding.rateappTxtSubtitle.text = getString(R.string.performBetter)
        }
        dialog_binding.ratingBar.rating = ratingExisted

        dialog_binding.rateBtn.setOnClickListener {
            val rating = dialog_binding.ratingBar.rating
            handleRatings(dialog, rating)
        }


        dialog_binding.reviewBtn.setOnClickListener {
            dialog_binding.ratingBar.setIsIndicator(false)
            dialog_binding.linearRated.visibility = View.GONE
            dialog_binding.linearNotRated.visibility = View.VISIBLE
        }

        dialog_binding.doneId.setOnClickListener {
            dialog.dismiss()
            hideNavBar()

        }
        dialog_binding.doItLater.setOnClickListener {
            dialog.dismiss()
//            finishAffinity()
        }
        dialog_binding.closeDialog.setOnClickListener {
            dialog.dismiss()
            hideNavBar()

        }
    }

    private fun handleRatings(rateUsDialog: Dialog, rating: Float) {
        when {
            rating == 0.0f -> {
                nonZeroRating=false
                Toast.makeText(
                    this@MainActivity,
                    R.string.kindly_rate_our_application,
                    Toast.LENGTH_SHORT
                ).show()
            }

            rating <= 1.0f -> {
                setRating(ratingKey, rating)
                rateUsDialog.dismiss()
                hideNavBar()
            }

            rating <= 2.0f -> {
                setRating(ratingKey, rating)
                rateUsDialog.dismiss()
                hideNavBar()
            }

            rating <= 3.0f -> {
                setRating(ratingKey, rating)
                rateUsDialog.dismiss()
                hideNavBar()
            }

            rating <= 4.0f -> {
                setRating(ratingKey, rating)
                rateUsDialog.dismiss()
                hideNavBar()
            }

            rating == 5.0f -> {
                setRating(ratingKey, rating)
                rateUsDialog.dismiss()
                val appPackageName = packageName
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$appPackageName")
                        )
                    )
                } catch (e: android.content.ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }
                hideNavBar()
            }

            else -> {
                setRating(ratingKey, rating)
                rateUsDialog.dismiss()
                hideNavBar()
            }


        }

        if(rating!=5.0f && nonZeroRating){
            feedBack(rating)
        }
    }
    private fun setRating(key: String, value: Float) {
        Paper.book().write("$key", value)
        nonZeroRating=true
    }

    override fun onResume() {
        super.onResume()
        (application as MainApplication).loadAd(this)
        adView.resume()
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onPause() {
        super.onPause()
        adView.pause()

        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun feedBack(rating:Float){
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:alawraqmarketing@gmail.com")
            putExtra(Intent.EXTRA_SUBJECT, "Rating for your app: ${rating.toString()}")
        }
        startActivity(Intent.createChooser(emailIntent, "Send feedback"))
    }

    private fun privacyPolicy(){
        val privacyPolicyUrl = "https://sites.google.com/view/alawraq-studio/home"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
        startActivity(browserIntent)
    }

//    private fun loadBanner() {
//        adView.adUnitId = BuildConfig.banner_home
//        adView.setAdSize(adSize)
//        adView.background=getDrawable(R.color.white)
//        val adRequest = AdRequest.Builder().build()
//        adView.loadAd(adRequest)
//    }

}