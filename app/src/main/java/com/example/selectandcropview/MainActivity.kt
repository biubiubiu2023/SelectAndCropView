package com.example.selectandcropview

import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.SizeUtils
import com.example.selectandcropview.data.Pos
import com.example.selectandcropview.data.SelectedRect
import com.example.selectandcropview.databinding.ActivityMainBinding
import com.example.selectandcropview.utils.ViewUtils
import com.example.selectandcropview.utils.setRoundConner

class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.confirm.setRoundConner(20)
        binding.confirm.setOnClickListener {
            if(binding.selectcrop.type == 0){
                if(binding.selectcrop.switchType(1)){
                    binding.confirm.text = "完成"
                }
            }else if(binding.selectcrop.type == 1){
                val datas = binding.selectcrop.cropAgent.datas
                ImageListActivity.start(this@MainActivity,datas)
            }
        }
        binding.image.post {
            ViewUtils.getImageRealPosition(binding.image).let { irp ->
                binding.selectcrop.setLimitedRect(
                    Rect(
                        irp.left.toInt(),
                        irp.top.toInt(),
                        irp.right.toInt(),
                        irp.bottom.toInt()
                    )
                )

                val opts = BitmapFactory.Options()
                opts.inJustDecodeBounds = true
                BitmapFactory.decodeResource(resources,R.drawable.background,opts)
                val size:IntArray = intArrayOf(opts.outWidth, opts.outHeight)

                val rects = mutableListOf<SelectedRect>()

                val space = SizeUtils.dp2px(70f)
                val cropHeight = SizeUtils.dp2px(50f)
                for (i in 4 downTo   1){
                    val pos = mutableListOf<Pos>()
                    pos.add(Pos(size[0]/5, space * i))
                    pos.add(Pos(size[0]*4/5, space * i))
                    pos.add(Pos(size[0]*4/5, space * i + cropHeight))
                    pos.add(Pos(size[0]/5, space * i + cropHeight))
                    val selectedRect = SelectedRect(
                        pos,
                        size,
                        irp
                    )
                    rects.add(selectedRect)
                }

                binding.selectcrop.setRects(rects)
            }
        }
    }

    override fun onBackPressed() {
        if(binding.selectcrop.type == 1 ){
            binding.selectcrop.switchType(0)
            binding.confirm.text = "下一步"
            return
        }
        super.onBackPressed()
    }
}