package com.example.demo.web.admin;


import com.example.demo.po.Type;
import com.example.demo.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class TypeController  {

    private final TypeService typeService;

    public TypeController(TypeService typeService) {
        this.typeService = typeService;
    }

    @GetMapping("/types")
    public String types(@PageableDefault(size=5,sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable
                        , Model model){
        model.addAttribute("page",typeService.listType(pageable)); //将数据传递到前端
        return "admin/types";
    }

    @GetMapping("/types/input")
    public String input(Model model){
        model.addAttribute("type",new Type()); //这里传递了一个空type，id也是null
        return "admin/types-new";
    }

    @PostMapping("/types")
    public String post(@Valid Type type, BindingResult result, RedirectAttributes attributes){ //bindingResult必须放在type后面

        Type type1 = typeService.getTypeByName(type.getName()); //后端验证类型是否重复,传递给前端的error message
        if(type1 != null){  //查到后台有数据，type1不为空，所以报错
            result.rejectValue("name","nameError","不能添加相同分类");
        }

        if(result.hasErrors()){          //type不为空和type重复都会有error
            return "admin/types-new";
        }

        Type t = typeService.saveType(type);
        if(t == null){
            attributes.addFlashAttribute("message","添加失败");
        }else{
            attributes.addFlashAttribute("message","添加成功");
        }
        return "redirect:/admin/types";//直接返回admin/types不会执行上面的types方法，报错
    }

    @GetMapping("/types/{id}/input")  //跳转到修改类型，这个id是从前台传过来的
    public String editInput(@PathVariable Long id , Model model){
        model.addAttribute("type",typeService.getType(id).get());
        return "admin/types-new";
    }

    @PostMapping("/types/{id}")   //前台post传递id到后台，用来修改type
    public String editPost(@Valid Type type, BindingResult result,@PathVariable Long id, RedirectAttributes attributes){ //bindingResult必须放在type后面

        Type type1 = typeService.getTypeByName(type.getName()); //后端验证类型是否重复,传递给前端的error message
        if(type1 != null){  //查到后台有数据，type1不为空，所以报错
            result.rejectValue("name","nameError","不能添加相同分类");
        }

        if(result.hasErrors()){          //type不为空和type重复都会有error
            return "admin/types-new";
        }

        Type t = typeService.updateType(id,type);
        if(t == null){
            attributes.addFlashAttribute("message","更新失败");
        }else{
            attributes.addFlashAttribute("message","更新成功");
        }
        return "redirect:/admin/types";//直接返回admin/types不会执行上面的types方法，报错
    }
    @GetMapping("/types/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes){
        typeService.delete(id);
        attributes.addFlashAttribute("message","删除成功");
        return "redirect:/admin/types";
    }
}
