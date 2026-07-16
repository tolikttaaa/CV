%% ============================================================================
%% cvdsl.cls — CV document class for the cv-dsl LaTeX renderer
%%
%% Ships as a bundled resource of the cv-dsl library; LatexRenderer extracts it
%% (together with the fonts/ directory) next to the generated sources and emits
%% documents targeting the commands defined here. The public surface consumed
%% by the renderer is:
%%
%%   header:    \name \tagline \photo \socialinfo \makecvheader \makecvfooter
%%   sections:  \sectionTitle + the summary / works / projects / keywords /
%%              education / referees environments and their entry commands
%%   inline:    \link (colored hyperlink), \cvtag (rounded keyword chip)
%%
%% Class options:
%%   green | red | indigo | orange  – accent color themes
%%   monochrome                     – black accent (print-friendly)
%%   localFont                      – load Source Sans Pro from fonts/ instead
%%                                    of the system-installed family
%%   alternative                    – compact header: tagline under the name
%%                                    (default: centered tagline banner)
%%   showLinks                      – draw the default hyperlink borders
%% ============================================================================

\ProvidesClass{cvdsl}[2026/07/07 CV class for the cv-dsl generator]

% Letter paper matches the fixed layout lengths used below.
\LoadClass[letterpaper]{article}

%% ─── Options ────────────────────────────────────────────────────────────────

\newif\if@showLinks \@showLinksfalse

\DeclareOption{green}      {\def\@green{green}}
\DeclareOption{red}        {\def\@red{red}}
\DeclareOption{indigo}     {\def\@indigo{indigo}}
\DeclareOption{orange}     {\def\@orange{orange}}
\DeclareOption{monochrome} {\def\@monochrome{monochrome}}
\DeclareOption{localFont}  {\def\@local{local}}
\DeclareOption{alternative}{\def\@alternative{alternate}}
\DeclareOption{showLinks}  {\@showLinkstrue}

\ProcessOptions

%% ─── Packages ───────────────────────────────────────────────────────────────

\RequirePackage[english]{babel}               % hyphenation patterns, dates
\RequirePackage{fontspec}                     % OpenType fonts (LuaLaTeX)
\RequirePackage{parskip}                      % paragraph spacing instead of indent
\RequirePackage[dvipsnames]{xcolor}           % named colors + color mixing
\RequirePackage[margin=1.5cm]{geometry}       % page margins
\RequirePackage{fontawesome5}                 % \fa* icons for titles and contacts
\RequirePackage{hyperref}                     % clickable links, PDF metadata
\RequirePackage{titlesec}                     % \section format (rule under title)
\RequirePackage{enumitem}                     % compact bullet lists
\RequirePackage{longtable}                    % section body containers
\RequirePackage{etoolbox}                     % \ifundef / \ifdef option tests
\RequirePackage{tikz}                         % \cvtag chips + \foreach over tags
\RequirePackage{graphicx}                     % profile photo
\RequirePackage{fancyhdr}                     % footer (name / page number)

%% ─── Accent color ───────────────────────────────────────────────────────────
%% One base color selected by the theme options (default: indigo-ish navy);
%% link, accent and symbol colors are tints of it.

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
%% Links stay clickable either way; showLinks merely keeps the border boxes.

\if@showLinks
  \hypersetup{breaklinks}
\else
  \hypersetup{pdfborder={0 0 0}, breaklinks}
\fi

%% ─── Page style ─────────────────────────────────────────────────────────────
%% Empty header, footer filled by \makecvfooter.

\fancyhf{}
\fancyhfoffset{0em}
\renewcommand{\headrulewidth}{0pt}
\pagestyle{fancy}

%% ─── Section formatting ─────────────────────────────────────────────────────
%% Large left-aligned titles with a full-width rule underneath, tight spacing.

\titleformat{\section}{\Large\raggedright}{}{0em}{}[\titlerule]
\titlespacing{\section}{0pt}{2pt}{2pt}

%% ─── List style ─────────────────────────────────────────────────────────────
%% Achievement bullets: angle-arrow markers, no extra vertical space.

\setlist[itemize,1]{label=\faAngleRight, nosep, leftmargin=2em}
\setlist[itemize,2]{label=\faAngleRight, nosep, leftmargin=1.5em}

%% ─── Fonts ──────────────────────────────────────────────────────────────────
%% Source Sans Pro; Light as the body weight, Regular as its bold companion.
%% With the localFont option the OTF files bundled under fonts/ are used, so
%% the document compiles without the family being installed system-wide.

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

%% ─── Icon aliases ───────────────────────────────────────────────────────────
%% Semantic names for the FontAwesome glyphs used by the contact and referee
%% commands, so a glyph can be swapped in one place.

\newcommand{\mailSymbol}    {\faAt}
\newcommand{\chatSymbol}    {\faPaperPlane}
\newcommand{\locationSymbol}{\faMapMarked}
\newcommand{\markerSymbol}  {\faMapMarker}
\newcommand{\infoSymbol}    {\faInfo}
\newcommand{\linkedinSymbol}{\faLinkedin}
\newcommand{\leetcodeSymbol}{\faLink}
\newcommand{\mobileSymbol}  {\faMobile}
\newcommand{\githubSymbol}  {\faGithub}

%% ─── Inline helpers ─────────────────────────────────────────────────────────

% \link{url}{label} — hyperlink typeset in the theme link color.
\newcommand\link[2]{\color{linkcolor}\href{#1}{#2}\color{Black}}

% \cvtag{text} — small rounded-corner chip used for technology keywords.
\newcommand{\cvtag}[1]{%
  \tikz[baseline]\node[anchor=base, draw=darkGrey!70, rounded corners=0.5ex,
    inner xsep=1ex, inner ysep=0.55ex, text height=1.3ex, text depth=.25ex]{#1};%
}

%% ─── Contact commands ───────────────────────────────────────────────────────
%% Building blocks: icon + text, optionally wrapped in a hyperlink. Each entry
%% is an unbreakable box so the contact rows wrap only between entries.

\newcommand{\socialtext}[2]{\mbox{\textcolor{symbolcolor}{#1}\hspace{0.5em}#2\hspace{1em}}}
\newcommand{\sociallink}[3]{\mbox{\textcolor{symbolcolor}{#1}\hspace{0.5em}\link{#2}{#3}\hspace{1em}}}

\newcommand*{\address}   [1]{\socialtext{\locationSymbol}{#1}}
\newcommand*{\smartphone}[1]{\socialtext{\mobileSymbol}{#1}}
\newcommand*{\email}     [1]{\sociallink{\mailSymbol}{mailto:#1}{#1}}
\newcommand*{\linkedin}  [1]{\sociallink{\linkedinSymbol}{https://www.linkedin.com/in/#1}{linkedin.com/in/#1}}
\newcommand*{\github}    [1]{\sociallink{\githubSymbol}{https://github.com/#1}{github.com/#1}}
\newcommand*{\telegram}  [1]{\sociallink{\chatSymbol}{https://t.me/#1}{t.me/#1}}
\newcommand*{\leetcode}  [1]{\sociallink{\leetcodeSymbol}{https://leetcode.com/u/#1}{leetcode.com/u/#1}}

%% ─── Header metadata ────────────────────────────────────────────────────────
%% Setters called in the preamble; \makecvheader renders from the stored values.

% \name{first}{last}
\newcommand*{\name}[2]{\def\@firstname{#1}\def\@lastname{#2}}

% \tagline{professional title}
\newcommand*{\tagline}[1]{\def\@tagline{#1}}

% \photo{diameter}{file} — profile photo and the column width reserved for it.
\newcommand*{\photo}[2]{\def\@photo{#2}\def\@photodiameter{#1}}

% \socialinfo{rows} — contact entries, rows separated with \\.
\newcommand{\socialinfo}[1]{\def\@socialinfo{#1}}

%% ─── Header rendering ───────────────────────────────────────────────────────

% First name in accent color, last name additionally bold small-caps.
\newcommand\user[2]{\color{accentcolor}{\LARGE #1 \textsc{\textbf{#2}}}\color{Black}}

% Tagline: centered banner by default, inline line in the alternative header.
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

% \makecvheader — name, tagline and contacts, with the photo (when set) in a
% right-hand column of \@photodiameter width. Two layouts, see the
% `alternative' class option.
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

% \makecvfooter{left}{center}{right}
\newcommand*{\makecvfooter}[3]{%
  \fancyfoot{}
  \fancyfoot[L]{#1}
  \fancyfoot[C]{#2}
  \fancyfoot[R]{#3}
}

%% ─── Section title ──────────────────────────────────────────────────────────

% \sectionTitle{title}{\faIcon} — accent-colored icon + small-caps title;
% the PDF bookmark gets the plain text.
\newcommand\sectionTitle[2]{%
  \section{\texorpdfstring{\color{accentcolor}#2\enspace\textsc{#1}}{#1}}%
}

%% ─── Section body layout ────────────────────────────────────────────────────
%% Every prose section (summary, works, projects, education) is a single-column
%% longtable whose rows are minipages of width \linewidth - 1.5em. longtable
%% centers its content, which gives all these sections one common left edge —
%% keep that invariant when adding new section types.

%% ─── Summary ────────────────────────────────────────────────────────────────

\newenvironment{summary}{%
  \begin{longtable}{l}%
}{%
  \end{longtable}%
}

% \summaryText{text}
\newcommand\summaryText[1]{%
  \begin{minipage}[t]{\dimexpr(\linewidth) - 1.5em}#1\end{minipage}\\*%
}

%% ─── Keywords (skills table) ────────────────────────────────────────────────

\newenvironment{keywords}{%
  \renewcommand{\arraystretch}{1.1}%
  \begin{tabular}{r p{13cm}}%
}{%
  \end{tabular}%
}

% \keywordsentry{category}{comma-separated values}
\newcommand\keywordsentry[2]{\textbf{#1} & #2\\}

%% ─── Works (experience / teaching) ──────────────────────────────────────────

\newenvironment{works}{%
  \begin{longtable}{l}%
}{%
  \end{longtable}%
}

% \work{role}{company}{location}{dates}{description}{tags}
% Header line: bold role, small-caps company, location — dates flushed right.
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

% \project{name}{company}{dates}{description}{tags}
\newcommand\project[5]{%
  \begin{minipage}[t]{\dimexpr(\linewidth) - 1.5em}
    \textbf{\textsc{#1}}, #2\hfill\textbf{#3}\smallskip\\
    #4\par\smallskip
    \footnotesize{\foreach \n in {#5}{\cvtag{\n}\ }}\\
  \end{minipage}\\*%
}

%% ─── Education ──────────────────────────────────────────────────────────────
%% Two-column rows inside the shared section layout: a right-aligned bold dates
%% column sized to exactly a "YYYY – YYYY" range (measured in bold at document
%% start, once the font is loaded), and a description column taking the rest.

\newlength{\educolumn}
\AtBeginDocument{\settowidth{\educolumn}{\textbf{0000 – 0000}}}

\newenvironment{education}{%
  \begin{longtable}{l}%
}{%
  \end{longtable}%
}

% \educationentry{years}{description}
\newcommand\educationentry[2]{%
  \begin{minipage}[t]{\educolumn}\raggedleft\textbf{#1}\end{minipage}\hspace{1em}%
  \begin{minipage}[t]{\dimexpr(\linewidth) - 1.5em - \educolumn - 1em}#2\end{minipage}\\%
}

%% ─── Referees ───────────────────────────────────────────────────────────────
%% Up to three references side by side, each a third of the line wide.

\newenvironment{referees}{}{\hfill\bigskip}

% \referee{name}{role}{company}{period}{email}
\newcommand*\referee[5]{%
  \hspace{1em}%
  \begin{minipage}[t]{\dimexpr(\linewidth/3) - 3em}
    \textbf{#1}\\[2pt]
    {\small\textcolor{symbolcolor}{\infoSymbol}\enspace\emph{#2}}\\[1pt]
    {\small\textcolor{symbolcolor}{\markerSymbol}\enspace\textsc{#3}\enspace(#4)}\\[1pt]
    {\small\textcolor{symbolcolor}{\mailSymbol}\enspace\href{mailto:#5}{#5}}
  \end{minipage}%
}
