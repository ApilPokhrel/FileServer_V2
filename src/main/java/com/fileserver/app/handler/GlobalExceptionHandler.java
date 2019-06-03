package com.fileserver.app.handler;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(value = Unauthorized.class)
    public ResponseEntity exception(Unauthorized exception) {
        return  ResponseEntity.status(403).body("unauthorized");
    }

    @ExceptionHandler(value = UnauthorizedUi.class)
    public void ui(UnauthorizedUi exception, HttpServletResponse response) throws IOException {
        response.sendRedirect("/login");
        return;
    }

//    @ExceptionHandler
//    @ResponseBody
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public Map handle(MethodArgumentNotValidException exception) {
//        return error(exception.getBindingResult().getFieldErrors()
//                .stream()
//                .map(FieldError::getDefaultMessage)
//                .collect(Collectors.toList()));
//    }


//    @ExceptionHandler
//    @ResponseBody
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public Map handle(ConstraintViolationException exception) {
//        return error(exception.getConstraintViolations()
//                .stream()
//                .map(ConstraintViolation::getMessage)
//                .collect(Collectors.toList()));
//    }

//    private Map error(Object message) {
//        return Collections.singletonMap("error", message);
//    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleError404(HttpServletRequest request, Exception e)   {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Request: " + request.getRequestURL() + " raised " + e);
        return new ModelAndView("404");
    }

//    @ExceptionHandler(Exception.class)
//    public ModelAndView handleError(HttpServletRequest req,HttpServletResponse res, Exception e)   {
//        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Request: " + req.getRequestURL() + " raised " + e);
//        return new ModelAndView("error");
//    }


}