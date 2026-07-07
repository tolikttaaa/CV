\ProvidesClass{documentMETADATA}

\LoadClass[letterpaper]{article}

%% ─── Options ────────────────────────────────────────────────────────────────

\newif\if@showLinks \@showLinksfalse

\DeclareOption{green}     {\def\@green{green}}
\DeclareOption{red}       {\def\@red{red}}
\DeclareOption{indigo}    {\def\@indigo{indigo}}
\DeclareOption{orange}    {\def\@orange{orange}}
\DeclareOption{monochrome}{\def\@monochrome{monochrome}}
\DeclareOption{localFont} {\def\@local{local}}
\DeclareOption{alternative}{\def\@alternative{alternate}}
\DeclareOption{showLinks} {\@showLinkstrue}

\ProcessOptions

%% ─── Packages ───────────────────────────────────────────────────────────────

\RequirePackage[english]{babel}
\RequirePackage[utf8]{luainputenc}
\RequirePackage{fontspec}
\RequirePackage{url,parskip}
\RequirePackage[dvipsnames]{xcolor}
\RequirePackage[margin=1.5cm]{geometry}
\RequirePackage{fontawesome5}
\RequirePackage{hyperref}
\RequirePackage{titlesec}
\RequirePackage{array}
\RequirePackage{enumitem}
\RequirePackage{longtable}
\RequirePackage{etoolbox}
\RequirePackage{tikz}
\RequirePackage{graphicx}
\RequirePackage{fancyhdr}

%% ─── Accent colour ──────────────────────────────────────────────────────────

\definecolor{basecolor}{HTML}{000066}

\ifundef{\@green}{%
  \ifundef{\@red}{%
    \ifundef{\@indigo}{%
      \ifundef{\@orange}{%
        \ifundef{\@monochrome}{}{\definecolor{basecolor}{HTML}{000000}}%
      }{\definecolor{basecolor}{HTML}{e68a00}}%
    }{\definecolor{basecolor}{rgb}{0.2,0.07,0.48}}%
  }{\definecolor{basecolor}{HTML}{b30000}}%
}{\definecolor{basecolor}{rgb}{0.0,0.62,0.38}}

\definecolor{darkGrey}{HTML}{989898}

\colorlet{linkcolor}  {basecolor}
\colorlet{accentcolor}{linkcolor!90}
\colorlet{symbolcolor}{linkcolor!85}

%% ─── Hyperref ───────────────────────────────────────────────────────────────

\if@showLinks
  \hypersetup{breaklinks}
\else
  \hypersetup{pdfborder={0 0 0}, breaklinks}
\fi

%% ─── Page style ─────────────────────────────────────────────────────────────

\fancyhf{}
\fancyhfoffset{0em}
\renewcommand{\headrulewidth}{0pt}
\pagestyle{fancy}

%% ─── Section formatting ─────────────────────────────────────────────────────

\titleformat{\section}{\Large\raggedright}{}{0em}{}[\titlerule]
\titlespacing{\section}{0pt}{2pt}{2pt}

%% ─── List style ─────────────────────────────────────────────────────────────

\setlist[itemize,1]{label=\faAngleRight, nosep, leftmargin=2em}
\setlist[itemize,2]{label=\faAngleRight, nosep, leftmargin=1.5em}

%% ─── Column types ───────────────────────────────────────────────────────────

\newcolumntype{R}[1]{>{\hfill}m{#1}}
\newcolumntype{E}{>{\raggedright\arraybackslash}p{\rightcolumnlength}}
\newcolumntype{Y}{>{\raggedleft}p{\leftcolumn}}

%% ─── Fonts ───────────────────────────────────────────────────────────────────

\defaultfontfeatures{Ligatures=TeX}

\ifundef{\@local}{%
  \setmainfont{Source Sans Pro Light}[
    BoldFont   = SourceSansPro-Regular,
    ItalicFont = Source Sans Pro Light Italic]
}{%
  \setmainfont{SourceSansPro-Light}[
    Path       = fonts/,
    BoldFont   = SourceSansPro-Regular,
    ItalicFont = SourceSansPro-LightIt]
}

%% ─── Layout lengths ─────────────────────────────────────────────────────────

\newlength{\rightcolumnlength}
\setlength{\rightcolumnlength}{14.8cm}

\newlength{\leftcolumn}
\setlength{\leftcolumn}{2.5cm}

%% ─── Icon aliases ───────────────────────────────────────────────────────────

\newcommand{\mailSymbol}    {\faAt}
\newcommand{\chatSymbol}    {\faPaperPlane}
\newcommand{\locationSymbol}{\faMapMarked}
\newcommand{\markerSymbol}  {\faMapMarker}
\newcommand{\infoSymbol}    {\faInfo}
\newcommand{\linkedinSymbol}{\faLinkedin}
\newcommand{\leetcodeSymbol}{\faLink}
\newcommand{\mobileSymbol}  {\faMobile}
\newcommand{\githubSymbol}  {\faGithub}
\newcommand{\linkSymbol}    {\faLink}

%% ─── Utility commands ───────────────────────────────────────────────────────

\newcommand\link[2]{\color{linkcolor}\href{#1}{#2}\color{Black}}
\newcommand{\cvtag}[1]{%
  \tikz[baseline]\node[anchor=base, draw=darkGrey!70, rounded corners=0.5ex,
    inner xsep=1ex, inner ysep=0.55ex, text height=1.3ex, text depth=.25ex]{#1};%
}

%% ─── Social commands ────────────────────────────────────────────────────────

\newcommand{\socialtext}[2]{\mbox{\textcolor{symbolcolor}{#1}\hspace{0.5em}#2\hspace{1em}}}
\newcommand{\sociallink}[3]{\mbox{\textcolor{symbolcolor}{#1}\hspace{0.5em}\link{#2}{#3}\hspace{1em}}}

\newcommand*{\address}    [1]{\socialtext{\locationSymbol}{#1}}
\newcommand*{\smartphone} [1]{\socialtext{\mobileSymbol}{#1}}
\newcommand*{\email}      [1]{\sociallink{\mailSymbol}{mailto:#1}{#1}}
\newcommand*{\linkedin}   [1]{\sociallink{\linkedinSymbol}{https://www.linkedin.com/in/#1}{linkedin.com/in/#1}}
\newcommand*{\github}     [1]{\sociallink{\githubSymbol}{https://www.github.com/#1}{github.com/#1}}
\newcommand*{\telegram}   [1]{\sociallink{\chatSymbol}{https://t.me/#1}{t.me/#1}}
\newcommand*{\leetcode}   [1]{\sociallink{\leetcodeSymbol}{https://leetcode.com/u/#1}{leetcode.com/u/#1}}
\newcommand*{\personalLink}[1]{\sociallink{\linkSymbol}{http://www.#1}{#1}}
\newcommand*{\website}    [2]{\sociallink{\faExternalLink}{#1}{#2}}

%% ─── Header metadata ────────────────────────────────────────────────────────

\newcommand*{\name}      [2]{\def\@firstname{#1}\def\@lastname{#2}}
\newcommand*{\firstname} [1]{\def\@firstname{#1}}
\newcommand*{\lastname}  [1]{\def\@lastname{#1}}
\newcommand*{\familyname}[1]{\def\@lastname{#1}}
\def\@familyname{\@lastname}

\newcommand*{\tagline}   [1]{\def\@tagline{#1}}
\newcommand*{\photo}     [2]{\def\@photo{#2}\def\@photodiameter{#1}}
\newcommand {\socialinfo}[1]{\def\@socialinfo{#1}}

%% ─── Header rendering ───────────────────────────────────────────────────────

\newcommand\user[2]{\color{accentcolor}{\LARGE #1 \textsc{\textbf{#2}}}\color{Black}}

\newcommand\resumetitle[1]{%
  \ifundef{\@alternative}{%
    \par{\bigskip\center{\Large\color{accentcolor}\textbf{#1}\color{Black}}\par}\bigskip
  }{%
    \color{accentcolor}\textbf{\large{#1}}\color{black}%
  }%
}

\newcommand\idphoto{%
  \includegraphics[width=\linewidth]{\@photo}%
}

\newcommand*{\makecvheader}{%
  \ifundef{\@alternative}{%
    \ifundef{\@photodiameter}{%
      \begin{minipage}{\linewidth}%
    }{%
      \begin{minipage}{\dimexpr\linewidth-\@photodiameter-2em}%
    }%
    \color{accentcolor}\user{\@firstname}{\@lastname}\color{black}\\
    \small{\@socialinfo}
    \end{minipage}%
    \ifdef{\@photodiameter}{%
      \begin{minipage}{\@photodiameter}\idphoto\end{minipage}%
    }{}%
    \resumetitle{\@tagline}%
  }{%
    \ifundef{\@photodiameter}{%
      \begin{minipage}{\linewidth}%
    }{%
      \begin{minipage}{\dimexpr\linewidth-\@photodiameter-2em}%
    }%
    \user{\@firstname}{\@lastname}\\
    \resumetitle{\@tagline}
    \smallskip\\
    \small{\@socialinfo}
    \end{minipage}%
    \ifdef{\@photodiameter}{%
      \begin{minipage}{\@photodiameter}\idphoto\end{minipage}%
    }{}%
    \bigskip
  }%
}

\newcommand*{\makecvfooter}[3]{%
  \fancyfoot{}
  \fancyfoot[L]{#1}
  \fancyfoot[C]{#2}
  \fancyfoot[R]{#3}
}

%% ─── Section title ───────────────────────────────────────────────────────────

\newcommand\sectionTitle[2]{%
  \section{\texorpdfstring{\color{accentcolor}#2\enspace\textsc{#1}}{#1}}%
}

%% ─── Summary ────────────────────────────────────────────────────────────────

\newenvironment{summary}{%
  \begin{longtable}{l}%
}{%
  \end{longtable}%
}

\newcommand\summaryText[1]{%
  \begin{minipage}[t]{\dimexpr(\linewidth) - 1.5em}#1\end{minipage}\\*%
}

%% ─── Keywords (skills table) ────────────────────────────────────────────────

\newenvironment{keywords}{%
  \renewcommand{\arraystretch}{1.1}%
  \begin{tabular}{>{}r>{}p{13cm}}%
}{%
  \end{tabular}%
}

\newcommand\keywordsentry[2]{\textbf{#1} & #2\\}

%% ─── Works (experience / teaching) ─────────────────────────────────────────

\newenvironment{works}{%
  \begin{longtable}{l}%
}{%
  \end{longtable}%
}

\newcommand\work[6]{%
  \noindent
  \makebox[\dimexpr(\linewidth) - 1.5em][l]{\textbf{#1, \textsc{#2}, #3}\hfill\textbf{#4}}\\
  \begin{minipage}[t]{\dimexpr(\linewidth) - 1.5em}
    \begin{minipage}[t]{\dimexpr(\linewidth)}#5\end{minipage}\\
    \footnotesize{\foreach \n in {#6}{\cvtag{\n}\ }}\\
  \end{minipage}\\*%
}

%% ─── Projects ───────────────────────────────────────────────────────────────

\newenvironment{projects}{%
  \begin{longtable}{l}%
}{%
  \end{longtable}%
}

\newcommand\project[5]{%
  \begin{minipage}[t]{\dimexpr(\linewidth) - 1.5em}
    \textbf{\textsc{#1}}, #2\hfill\textsc{#3}\smallskip\\
    #4\par\smallskip
    \footnotesize{\foreach \n in {#5}{\cvtag{\n}\ }}\\
  \end{minipage}\\*%
}

%% ─── Education ───────────────────────────────────────────────────────────────

\newenvironment{education}{%
  \begin{tabular}{Y p{\rightcolumnlength}}%
}{%
  \end{tabular}%
}

\newcommand\educationentry[2]{#1 & #2\\}

%% ─── Referees ────────────────────────────────────────────────────────────────

\newenvironment{referees}{}{\hfill\bigskip}

\newcommand*\referee[5]{%
  \hspace{1em}%
  \begin{minipage}[t]{\dimexpr(\linewidth/3) - 3em}
    \textbf{#1}\\[2pt]
    {\small\textcolor{symbolcolor}{\infoSymbol}\enspace\emph{#2}}\\[1pt]
    {\small\textcolor{symbolcolor}{\markerSymbol}\enspace\textsc{#3}\enspace(#4)}\\[1pt]
    {\small\textcolor{symbolcolor}{\mailSymbol}\enspace\href{mailto:#5}{#5}}
  \end{minipage}%
}

%% ─── Misc helpers ────────────────────────────────────────────────────────────

\newcommand\emptySeparator{\multicolumn{2}{c}{}\\}
