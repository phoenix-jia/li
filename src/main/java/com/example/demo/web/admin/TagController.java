package com.example.demo.web.admin;


import com.example.demo.po.Tag;
import com.example.demo.service.TagService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/tags")
    public String tags(@PageableDefault(size=5,sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable
                        , Model model){
        model.addAttribute("page",tagService.listTag(pageable)); //将数据传递到前端
        return "admin/tags";
    }

    @GetMapping("/tags/input")
    public String input(Model model){
        model.addAttribute("tag",new Tag()); //这里传递了一个空tag，id也是null
        return "admin/tags-new";
    }

    @PostMapping("/tags")
    public String post(@Valid Tag tag, BindingResult result, RedirectAttributes attributes){ //bindingResult必须放在tag后面

        Tag tag1 = tagService.getTagByName(tag.getName()); //后端验证类型是否重复,传递给前端的error message
        if(tag1 != null){  //查到后台有数据，tag1不为空，所以报错
            result.rejectValue("name","nameError","不能添加相同分类");
        }

        if(result.hasErrors()){          //tag不为空和tag重复都会有error
            return "admin/tags-new";
        }

        Tag t = tagService.saveTag(tag);
        if(t == null){
            attributes.addFlashAttribute("message","添加失败");
        }else{
            attributes.addFlashAttribute("message","添加成功");
        }
        return "redirect:/admin/tags";//直接返回admin/tags不会执行上面的tags方法，报错
    }

    @GetMapping("/tags/{id}/input")  //跳转到修改类型，这个id是从前台传过来的
    public String editInput(@PathVariable Long id , Model model){
        model.addAttribute("tag",tagService.getTag(id).get());
        return "admin/tags-new";
    }

    @PostMapping("/tags/{id}")   //前台post传递id到后台，用来修改tag
    public String editPost(@Valid Tag tag, BindingResult result,@PathVariable Long id, RedirectAttributes attributes){ //bindingResult必须放在type后面

        Tag tag1 = tagService.getTagByName(tag.getName()); //后端验证类型是否重复,传递给前端的error message
        if(tag1 != null){  //查到后台有数据，tag1不为空，所以报错
            result.rejectValue("name","nameError","不能添加相同分类");
        }

        if(result.hasErrors()){          //tag不为空和tag重复都会有error
            return "admin/tags-new";
        }

        Tag t = tagService.updateTag(id,tag);
        if(t == null){
            attributes.addFlashAttribute("message","更新失败");
        }else{
            attributes.addFlashAttribute("message","更新成功");
        }
        return "redirect:/admin/tags";//直接返回admin/tags不会执行上面的tags方法，报错
    }
    @GetMapping("/tags/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes){
        tagService.delete(id);
        attributes.addFlashAttribute("message","删除成功");
        return "redirect:/admin/tags";
    }
}
