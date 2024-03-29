package com.eina.as.controladores;

import com.eina.as.modelo.dataAccess.DAOColeccion;
import com.eina.as.modelo.dataAccess.DAOUsuario;
import com.eina.as.modelo.dataAccess.DAOVinilo;
import com.eina.as.modelo.service.Password;
import com.eina.as.modelo.service.Usuario;
import com.eina.as.modelo.service.Vinilo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


@Controller
public class ControladorCatalogo {



    // lo de value /catalogo que hace en verdad? Si cargo directamente la pagina catalogo.jsp cargaría este metodo?
    // Lo que creo casi seguro que hace es que si lo llamas con /catalogo o /catalogo2 va a hacerte una cosa de estas?

    @RequestMapping(value= "/catalogo")
    public String redireccionCatalogo(HttpServletRequest request/*, HttpServletResponse response*/)
            throws Exception{
        String resultado = "naa";
        request.getSession().setAttribute("resultado", resultado);
        request.removeAttribute("numPagina");
        DAOVinilo vin = new DAOVinilo();
        ArrayList <Vinilo> listaVinilos= vin.getListaVinilos(0);
        int numVinilos = vin.getNumeroVinilos();
        request.setAttribute ("numVinilos", numVinilos);
        request.setAttribute("listaVinilos", listaVinilos);
        request.getSession().setAttribute("numPagina","1");
        //  RequestDispatcher dispatcher = request.getRequestDispatcher("catalogo.jsp");
        //  dispatcher.forward(request,response);
        Usuario user = (Usuario) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/home";
        } else {
            return "catalogo";
        }

    }


    @RequestMapping(value= "/catalogor")
    public String redireccionCatalogor(HttpServletRequest request/*, HttpServletResponse response*/)
            throws Exception{
        request.removeAttribute("numPagina");
        DAOVinilo vin = new DAOVinilo();
        ArrayList <Vinilo> listaVinilos= vin.getListaVinilos(0);
        int numVinilos = vin.getNumeroVinilos();
        request.setAttribute ("numVinilos", numVinilos);
        request.setAttribute("listaVinilos", listaVinilos);
        request.getSession().setAttribute("numPagina","1");
        //  RequestDispatcher dispatcher = request.getRequestDispatcher("catalogo.jsp");
        //  dispatcher.forward(request,response);
        Usuario user = (Usuario) request.getSession().getAttribute("user");
        String numPagina = (String) request.getSession().getAttribute("numPagina");

        if (user == null) {
            return "redirect:/home";
        } else {
            int numPag = Integer.parseInt(numPagina);
            if(numPag>0){
                numPag=numPag-1;
                numPagina = ""+numPag;
                request.getSession().setAttribute("numPagina",numPagina);
                return "redirect:/catalogo2";
            }

            return "catalogo";
        }

    }

    @RequestMapping(value= "/catalogo2")
    public String redireccionPerfil2(HttpServletRequest request/*, HttpServletResponse response*/)
            throws Exception{
        Usuario user = (Usuario) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/home";
        } else {
            String sPagina = (String) request.getSession().getAttribute("numPagina");

            int numPagina = Integer.parseInt(sPagina);
            System.out.println("numPagina= "+ numPagina);

            if ((sPagina==null)|| (sPagina.trim().equals("")) || sPagina.trim().equals("0")) {
                numPagina = 0;
            }
            else{
                numPagina++; // tiene que ser cuando haces el "Ver 25 más" que aumente el numPagina.
                sPagina = Integer.toString(numPagina);
                request.getSession().setAttribute("numPagina",sPagina);
            }


            DAOVinilo vin = new DAOVinilo();
            int numVinilos = vin.getNumeroVinilos();
            request.setAttribute("numVinilos", numVinilos);
            ArrayList <Vinilo> listaVinilos;
            if (numPagina==0){
                listaVinilos= vin.getListaVinilos(0);
            }
            else {
                listaVinilos = vin.getListaVinilos(numPagina - 1);
            }
            request.setAttribute("listaVinilos", listaVinilos);
            //  RequestDispatcher dispatcher = request.getRequestDispatcher("catalogo.jsp");
            //  dispatcher.forward(request,response);

            return "catalogo";
        }

    }

    @RequestMapping(value="/anadirVinilo", method= RequestMethod.POST)
    public void anadirVinilo(@RequestParam("nombre") String id,
                             HttpServletRequest request,
                             HttpServletResponse response) throws Exception{
        System.out.println("Me ha llegado la peticion de anadir vinilo");
        System.out.println("primer" + id);
        Usuario user;
        Vinilo vin;
        DAOColeccion daoColeccion = new DAOColeccion();
        PrintWriter out = response.getWriter();
        ArrayList<Vinilo> listaVins = (ArrayList<Vinilo>) request.getSession().getAttribute("listaVinilos");
        System.out.println("tamaño listavins "+ listaVins.size());
        user = (Usuario) request.getSession().getAttribute("user");
        //String numPagina = (String) request.getSession().getAttribute("numPagina");
        //int numPag = Integer.parseInt(numPagina);
        int numVin = Integer.parseInt(id);
        //System.out.println("numpag:"+ numPag);
        //int idVinilo = (numPag-1)*25 + numVin;
        //System.out.println("suma" +idVinilo);
        vin = listaVins.get(numVin-1);
        //DAOVinilo daoVinilo = new DAOVinilo();
        //vin = daoVinilo.getViniloByID(idVinilo);
        System.out.println("titulitis " + vin.getTitulo());
        if(!daoColeccion.existe(vin,user)){
            daoColeccion.insert(user,vin);
            System.out.println("exitico");
            String resultado = "exito";
            request.getSession().setAttribute("resultado", resultado);
            out.println("exito");
        }
        else{
            String resultado = "fracaso";
            System.out.println("fracasico");
            request.getSession().setAttribute("resultado", resultado);
            out.println("fracaso");
        }


    }

}


// comprobando que me haga algo el puto push